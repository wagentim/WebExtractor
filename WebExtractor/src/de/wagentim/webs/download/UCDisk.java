package de.wagentim.webs.download;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.wagentim.db.CookieHandler;
import de.wagentim.db.PersisCookie;
import de.wagentim.element.DownloadFile;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.webs.utils.Utils;

public class UCDisk extends AbstractWebsite{
	
	private final int client_id = 54;
	private final String userName = "bhwerbung@googlemail.com";
	private final String psw = "huang78";
	private static final String NAME = "uc_disk";
	
	private String service_ticket = "";
	
	public UCDisk()
	{
		super(NAME);
	}
	
	@Override
	public URI getBasicURI()
	{
		try {
			return new URIBuilder()
						.setScheme("https")
						.setHost("api.open.uc.cn")
						.setPath("/cas/loginByIframe")
						.addParameter("v", "1.1")
						.addParameter("client_id", ""+client_id)
						.addParameter("request_id", ""+System.currentTimeMillis())
						.addParameter("login_name", userName)
						.addParameter("password", psw).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void run() {
		
		clearCookie();
		
		processAll();
		
	}

	private void processAll() {
		
		// 1. step: Login Operation
		
		HttpResponse resp = handlerGet(getBasicURI(), getBaseHeader());
		
		String respContent = printResponseContent( resp, false );
		
		if( null == respContent || respContent.isEmpty() )
		{
			log.log("After sending Login Data we just get Null or empty HTML content", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		addCookies( CookieHandler.INSTANCE.saveCookies(resp, NAME, false) );	// get cookies
		
		// 2. step: check the result of login and if successful try to get service ticket
		
		String link = parserLoginResponseContent( respContent );
		
		if( null == link || link.isEmpty() )
		{
			log.log("Cannot get service ticket", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		service_ticket = getServiceTicket( Utils.decode(link) );
		
		
		if( null == service_ticket || service_ticket.isEmpty() )
		{
			log.log("Failed to get Service Ticket from: " + link, Log.LEVEL_ERROR);
			return;
		}
		
		// 3. step: try to get and "yunsess" value after the getting of service ticket
		resp = handlerGet(getYunsessURI(), null);
		
		addCookies(CookieHandler.INSTANCE.saveCookies(resp, NAME, false));
		
		PersisCookie yunsess = null;
		
		if( (yunsess = getCookie("yunsess")) != null )
		{
			log.log("Success to get yunsess: " + yunsess.toString(), Log.LEVEL_INFO);
		}else
		{
			log.log("Failed to get yunsess", Log.LEVEL_ERROR);
			return;
		}
		
		// 4. step: try to get dir id
		resp = handlerGet(getDiskURI(), null);
		
		respContent = printResponseContent( resp, false );
		
		List<String> dirs = parserDIRs(respContent);
		
		if( null == dirs || dirs.isEmpty() )
		{
			log.log("Cannot find Dir ID", Log.LEVEL_ERROR);
			return;
		}
		
		if( dirs.size() != 1 )
		{
			log.log("find more than one dir number", Log.LEVEL_WARN);
		}
		
		// 5. step: try to get file list
		String dir = dirs.get(0);
		
		resp = handlerGet(getFileListURI(dir), null);
		
		respContent = printResponseContent( resp, false );
		
		List<DownloadFile> files = getDownloadFiles(respContent);
		
		if( null == files || files.isEmpty() )
		{
			log.log("No File is parsered", Log.LEVEL_ERROR);
			
			return;
		}
		
		for( DownloadFile f : files )
		{
			if( getDownloadKey(f) )
			{
				// TODO pass the DownloadFile object to download handler
			}else
			{
				log.log("Failed to get real donwload link for the file" + f.getName(), Log.LEVEL_CRITICAL_ERROR);
				continue;
			}
		}
		
		// download the file
	}
	
	private boolean getDownloadKey(DownloadFile f) {
		
		if( null == f )
		{
			return false;
		}
		
		URI uri = null;
		
		try {
			uri = new URI(f.getDonwloadURL());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		HttpResponse resp = handlerGet(uri, null);
		
		if( null == resp )
		{
			log.log("For getting dl_key the response is null", Log.LEVEL_ERROR);
			return false;
		}
		
		Header[] headers = resp.getAllHeaders();
		
		for(Header h : headers)
		{
			if( h.getName().equals("Location") )
			{
				f.setDonwloadURL(h.getValue());
				return true;
				//TODO also save the expire data for this download.In case of starting broken download or download it later
			}
		}
		
		return false;
	}

	private List<String> parserDIRs(final String respContent) 
	{
		if( null == respContent || respContent.isEmpty() )
		{
			return null;
		}
		
		Document doc = Utils.getHTMLDocument(respContent);
		
		Elements hrefs = doc.select("a[href]");
		
		if( null == hrefs || hrefs.size() <= 0 )
		{
			return null;
		}
		
		List<String> result = new ArrayList<String>();
		
		for( Element e : hrefs )
		{
			String value = e.attr("dirid");
			
			if( null == value || value.isEmpty() )
			{
				continue;
			}
			
			if( !result.contains(value))
			{
				result.add(value);
			}
		}
		
		return result;
	}

	private URI getYunsessURI()
	{
		try {
			return new URIBuilder()
						.setScheme("http")
						.setHost("yun.uc.cn")
						.setPath("/exter/basicinfo")
						.addParameter("service_ticket", service_ticket).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private URI getFileListURI(final String dir)
	{
		try {
			return new URIBuilder()
						.setScheme("http")
						.setHost("disk.yun.uc.cn")
						.setPath("/netdisk/ajaxnd")
						.addParameter("dirid", dir).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private URI getDiskURI()
	{
		try {
			return new URIBuilder()
						.setScheme("http")
						.setHost("disk.yun.uc.cn").build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Header[] getBaseHeader() 
	{
		return new Header[]{new BasicHeader("Host", "api.open.uc.cn"), new BasicHeader("Referer", "http://yun.uc.cn/cloud/login")};
	}

	public static void main(String[] args)
	{
		new Thread(new UCDisk()).start();
	}
	
	/**
	 * <p>
	 * Parser the HTML file, which is returned after Login request. <br >
	 * If the login is successful, then try to get the service_ticket
	 * </p>
	 * @param input
	 * @return
	 */
	private String parserLoginResponseContent(final String input)
	{
		if( null == input || input .isEmpty() )
		{
			log.log("Returned HTML Content after loging request is Null or empty", Log.LEVEL_CRITICAL_ERROR);
			return null;
		}
		
		// check the status of login operation
		if( input.contains("Success") )
		{
			log.log("Login Success!", Log.LEVEL_INFO);
		}else
		{
			log.log("Login Failed!", Log.LEVEL_INFO);
			return null;
		}
		
		// try to find redirect link
		int hrefIndex = input.indexOf("href");
		
		if( -1 >= hrefIndex )
		{
			log.log("Can not find \"href\" key work in the reture Login HTML file", Log.LEVEL_ERROR);
			return null;
		}
		
		StringBuffer tmp = new StringBuffer(input.substring(hrefIndex));
		
		int oneIndex = tmp.indexOf("\"");
		
		if( oneIndex < 0)
		{
			log.log("Cannot find first \" in the text: " + tmp.toString(), Log.LEVEL_ERROR);
			return null;
		}
		
		tmp.delete(0, oneIndex + 1);
		
		oneIndex = tmp.indexOf("\"");
		
		if( oneIndex < 0)
		{
			log.log("Cannot find last \" in the text: " + tmp.toString(), Log.LEVEL_ERROR);
			return null;
		}
		tmp.delete(oneIndex, tmp.length());
		
		log.log("Find redirect link: " + tmp.toString(), Log.LEVEL_INFO);
		
		return tmp.toString();
	}
	
	private String getServiceTicket(final String input)
	{
		if( null == input || input.isEmpty() )
		{
			return null;
		}
		
		int respIndex = input.indexOf("response=");
		
		if( respIndex < 0 )
		{
			log.log("Cannot find key work response in: " + input, Log.LEVEL_ERROR);
			return null;
		}
		
		String result = input.substring(respIndex + "response=".length());
		
		JSONObject jo = new JSONObject(result);
		
		return ((JSONObject)jo.get("data")).getString("service_ticket");
	}
	
	private List<DownloadFile> getDownloadFiles(final String input)
	{
		if( null == input || input.isEmpty() )
		{
			return null;
		}
		
		JSONObject tmp = new JSONObject(input);

		JSONArray array = tmp.getJSONArray("filelist");
		
		if( null == array || array.length() < 0 )
		{
			return null;
		}
		
		List<DownloadFile> result = new ArrayList<DownloadFile>();
		
		for( int i = 0 ; i < array.length(); i++)
		{
			JSONObject o = array.getJSONObject(i);
			
			if( null == o )
			{
				continue;
			}
			
			result.add(assignValues(o));
		}
		
		return result;
	}

	private DownloadFile assignValues(final JSONObject o) {
		
		DownloadFile f = new DownloadFile();
		
		f.setDonwloadURL(o.getString("download"));
		f.setName(o.getString("name"));
		
		return f;
	}
	
}
