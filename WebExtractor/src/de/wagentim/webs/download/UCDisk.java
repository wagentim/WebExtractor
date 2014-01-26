package de.wagentim.webs.download;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import de.wagentim.db.CookieHandler;
import de.wagentim.qlogger.logger.Log;

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
		
		try {
			service_ticket = getServiceTicket( URLDecoder.decode(link, "utf-8") );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		if( null == service_ticket || service_ticket.isEmpty() )
		{
			log.log("Failed to get Service Ticket from: " + link, Log.LEVEL_ERROR);
			return;
		}
		
		// 3. step: try to get "yunsess" value after the getting of service ticket
		
		
		
		resp = handlerGet(getYunsessURI(), null);
		
//		URI secondURI = null;
//		
//		try {
//			secondURI = new URI(link);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		
//		if( null == secondURI )
//		{
//			log.log("Cannot create URI object from the return Link: " + link, Log.LEVEL_ERROR);
//			
//			return;
//		}
//		
//		resp = handlerGet(secondURI, null);
//		
//		printContent( resp, true );
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
}
