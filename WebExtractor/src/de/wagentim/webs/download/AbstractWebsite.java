package de.wagentim.webs.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import de.wagentim.connect.ConnectManager;
import de.wagentim.connect.RequestFactory;
import de.wagentim.db.PersisCookie;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;


public abstract class AbstractWebsite implements Website {
	
	private String webName = "NO_NAME";
	
	private int logID = -1;
	
	protected LogChannel log = null;
	
	protected Map<String, PersisCookie> cookies = null;
	
	public AbstractWebsite(final String name)
	{
		if( null != name && !name.isEmpty() )
		{
			this.webName = name;
		}
		
		cookies = new HashMap<String, PersisCookie>(15);
		logID = QLoggerService.addChannel(new DefaultChannel(null == name ? "" : name));
		log = getLog();
	}
	
	protected void clearCookie()
	{
		cookies.clear();
	}
	
	protected LogChannel getLog()
	{
		return logID < 0 ? null : QLoggerService.getChannel(logID);
	}
	
	@Override
	public RequestConfig getLocalRequestConfig()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(webName);
		sb.append(": ");
		sb.append(null == getBasicURI() ? "" : getBasicURI().toString());
		return sb.toString();
	}
	
	public void addRequestHeaders(HttpRequestBase request, Header[] headers)
	{
		if( null == request || null == headers || headers.length < 0 )
		{
			return;
		}
		
		for(Header header : headers)
		{
			request.addHeader(header);
			log.log("Add Header: " + header.toString(), Log.LEVEL_INFO);
		}
	}
	
	public boolean isSuccessful(HttpResponse response)
	{
		if( null == response )
		{
			log.log("The response is NULL", Log.LEVEL_CRITICAL_ERROR);
			
			return false;
		}
		
		int code = response.getStatusLine().getStatusCode();
		
		if( code == HttpStatus.SC_OK )
		{
			return true;
		}
		
		return false;
	}
	
	protected HttpRequestBase getHttpGet(final URI uri, Header[] headers)
	{
		log.log("Request URI: " + uri.toString(), Log.LEVEL_INFO);
		
		HttpRequestBase get = RequestFactory.getRequest(RequestFactory.TYPE_GET, cookies, logID);
		
		if( null == get )
		{
			log.log("The Request from Request Factory is NULL", Log.LEVEL_CRITICAL_ERROR);
			return null;
		}
		
		get.setURI(uri);
		
		addRequestHeaders(get, headers);
		
		return get;
	}
	
	protected HttpResponse handlerGet(final URI uri, Header[] headers)
	{
		HttpRequestBase get = getHttpGet(uri, headers);
		
		HttpResponse response = null;
		
		try {
			response = ConnectManager.sendRequest(get, null);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if( !isSuccessful(response) )
		{
			return null;
		}
		
		return response;
	}
	
	protected String printResponseContent(final HttpResponse response, boolean console)
	{
		if( null == response )
		{
			log.log("The response is NULL", Log.LEVEL_CRITICAL_ERROR);
			
			return null;
		}
		
		InputStreamReader sr = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			sr = new InputStreamReader(response.getEntity().getContent());
			br = new BufferedReader(sr);
			String s = null;
			
			while( (s = br.readLine() ) != null )
			{
				if( console )
				{
					System.out.println(s);
				}
				sb.append(s);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if( null != br )
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				br = null;
			}
			
			if( null != sr )
			{
				try {
					sr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				sr = null;
			}
		}
		
		return sb.toString();
	}
	
	protected void addCookies(List<PersisCookie> inputCookies)
	{
		if( null == inputCookies || inputCookies.isEmpty() )
		{
			return;
		}
		
		for( PersisCookie pc : inputCookies )
		{
			cookies.put(pc.getKey(), pc);
			log.log("Save Cookie: " + pc.toString(), Log.LEVEL_INFO);
		}
	}
	
	protected void addCookie(PersisCookie cookie)
	{
		if( null == cookie )
		{
			return;
		}
		
		cookies.put(cookie.getKey(), cookie);
		log.log("Save Cookie: " + cookie.toString(), Log.LEVEL_INFO);
	}
	
	protected PersisCookie getCookie(final String key)
	{
		return cookies.get(key);
	}
}
