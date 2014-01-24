package de.wagentim.connect;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

public class Connection {
	
	public static final int GET = 0;
	
	private CloseableHttpClient client = null;

	public Connection(CloseableHttpClient client) {
		
		this.client = client;
	}
	
	public CloseableHttpResponse getResponse(final int requireType, final URI uri, final RequestConfig localConfig )
	{

		HttpRequestBase request = null;
		
		switch(requireType)
		{
			case GET:
				request = new HttpGet(uri);
				break;
		}
		
		if( null != request )
		{
			if( null != localConfig )
			{
				request.setConfig(localConfig);
			}
			
			try {
				client.execute(request);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
