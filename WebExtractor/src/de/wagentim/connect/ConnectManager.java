package de.wagentim.connect;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectManager {
	
	private static PoolingHttpClientConnectionManager manager = null;
	private static final CloseableHttpClient client;
	private static RequestConfig globalConfig = null;
	
	static
	{
		manager = new PoolingHttpClientConnectionManager();
		globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
		client = HttpClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(globalConfig)
					.build();
	}
	
	public static HttpResponse sendRequest(final HttpRequestBase request, final Object object) throws ClientProtocolException, IOException
	{
		return client.execute(request);
	}
	
}
