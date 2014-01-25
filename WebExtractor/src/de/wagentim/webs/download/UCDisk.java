package de.wagentim.webs.download;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import de.wagentim.connect.RequestFactory;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.webs.utils.Utils;

public class UCDisk extends AbstractWebsite{
	
	private final int client_id = 54;
	private final String userName = "bhwerbung@googlemail.com";
	private final String psw = "huang78";
	private static final String NAME = "uc_disk";
	
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
						.addParameter("login_name", Utils.encode(userName))
						.addParameter("password", Utils.encode(psw)).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void run() {
		
		log.log("Request Basic URI: " + getBasicURI().toString(), Log.LEVEL_INFO);
		
		HttpRequestBase get = RequestFactory.getRequest(RequestFactory.TYPE_GET);
		
		if( null == get )
		{
			log.log("The Request from Request Factory is NULL", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		get.setURI(getBasicURI());
		
		log.log("Set Headers...", Log.LEVEL_INFO);
		
		addRequestHeaders(get);
		
	}
	
	@Override
	public void addRequestHeaders(HttpRequestBase request)
	{
		request.addHeader("Host", "api.open.uc.cn");
		request.addHeader("Referer", "http://yun.uc.cn/cloud/login");
	}

}
