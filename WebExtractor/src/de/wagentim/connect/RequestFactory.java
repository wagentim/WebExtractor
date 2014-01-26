package de.wagentim.connect;

import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import de.wagentim.db.PersisCookie;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public class RequestFactory {
	
	public static final int TYPE_GET = 0;
	
	private static final String COOKIE = "Cookie";
	
	public static HttpRequestBase getRequest(final int type, final Map<String, PersisCookie> cookies, final int logID)
	{
		
		HttpRequestBase result = getRequestInternal(type);
		
		if( null == result )
		{
			return null;
		}
		
		addCommonHeader(result);
		
		if( null != cookies && !cookies.isEmpty() )
		{
			attachCookies(result, cookies, logID);
		}
	
		return result;
		
	}
	
	private static void attachCookies(HttpRequestBase result,
			Map<String, PersisCookie> cookies, int logID) {
		
		StringBuffer sb = new StringBuffer();
		LogChannel log = QLoggerService.getChannel(logID);
		
		for( PersisCookie cookie : cookies.values() )
		{
			sb.append(cookie.toString());
			sb.append(";");
			if( null != log)
			{
				log.log("Add Cookie: " + cookie.toString(), Log.LEVEL_INFO);
			}
		}
		
		result.addHeader(COOKIE, sb.deleteCharAt(sb.lastIndexOf(";")).toString());
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
