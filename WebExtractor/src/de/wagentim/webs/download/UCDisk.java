package de.wagentim.webs.download;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;

import de.wagentim.connect.Connect;
import de.wagentim.connect.Connection;
import de.wagentim.webs.utils.Utils;

public class UCDisk extends AbstractWebSite{
	
	private final int client_id = 68;
	private final String userName = "bhwerbung@googlemail.com";
	private final String psw = "huang78";
	
	private static final String NAME = "uc_disk";
	
	protected RequestConfig rConfig = null;
	
	public UCDisk()
	{
		super();
	}
	
	@Override
	public URI getLoginURI()
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
	
	public static void main(String[] args)
	{
		WebSite uc = new UCDisk();
		uc.fetch();
	}

	@Override
	public void fetch() {
		
		CloseableHttpResponse response = Connect.getNewConnection().getResponse(Connection.GET, getLoginURI(), getLocalRequestConfig());
		
		if( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK )
		{
			return;
		}
		
	}
	
	@Override
	public RequestConfig getLocalRequestConfig()
	{
		if( null == rConfig )
		{
			rConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.).build();
		}
		
		return rConfig;
	}

}
