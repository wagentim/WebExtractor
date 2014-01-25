package de.wagentim.connect;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class RequestFactory {
	
	public static final int TYPE_GET = 0;
	
	public static HttpRequestBase getRequest(final int type)
	{
		
		HttpRequestBase result = getRequestInternal(type);
		
		if( null != result )
		{
			addCommonHeader(result);
		}
		
		return result;
		
	}
	
	private static void addCommonHeader(HttpRequestBase result) 
	{
		result.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		result.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		result.addHeader("Accept-Language", "zh-CN,zh;q=0.8,de;q=0.6,en;q=0.4,zh-TW;q=0.2");
		result.addHeader("Connection", "keep-alive");
		result.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36");
	}

	private static HttpRequestBase getRequestInternal(final int type)
	{
		switch(type)
		{
			case TYPE_GET:
				return new HttpGet();
		}
		
		return null;
	}
}
