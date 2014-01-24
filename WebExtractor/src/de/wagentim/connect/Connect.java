package de.wagentim.connect;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Connect {
	
	private static final CloseableHttpClient client = HttpClients.createDefault();
	
	public static Connection getNewConnection()
	{
		return new Connection(client);
	}
	
	public static CloseableHttpClient getClient()
	{
		return client;
	}
}
