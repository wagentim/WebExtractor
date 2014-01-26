package de.wagentim.webs.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public final class Utils {
	
	private static LogChannel log = QLoggerService.getChannel(QLoggerService.addChannel(new DefaultChannel("Utils")));
    
    public static List<NameValuePair> parserCookie(String value)
    {
    	if( null == value || value.isEmpty() )
    	{
    		return null;
    	}
    	
    	List<NameValuePair> result = new ArrayList<NameValuePair>();
    	
    	StringTokenizer st = new StringTokenizer(value, ";");
    	
    	while(st.hasMoreElements())
    	{
    		String pair = st.nextToken();
    		
    		int index = pair.indexOf("=");
    		
    		BasicNameValuePair unit = null;
    		
    		if( index > 0 )
    		{
    			String name = pair.substring(0, index).trim();
    			String val = pair.substring(index + 1, pair.length()).trim();
    			
    			if( null != name && null != val && !name.isEmpty() && !val.isEmpty() )
    			{
    				unit = new BasicNameValuePair(name, val);
    			}
    		}else
    		{
    			unit = new BasicNameValuePair(pair, "");
    		}
    		
    		if( null == unit )
    		{
    			continue;
    		}
    		
    		result.add(unit);
    	}
    	
    	return result;
    }
    
    public static Document getHTMLDocument(final String input)
    {
    	if( null == input || input.isEmpty() )
    	{
    		return null;
    	}
    	
    	Document d = null;
		
		d = Jsoup.parse(input);
		
		return d;
    }
    
    public static Map<String, String> getRequestParameters(final String input)
    {
    	if( null == input || input.isEmpty() )
    	{
    		return null;
    	}
    	
    	int qMark = input.indexOf("?");
    	
    	if( qMark < 0 )
    	{
    		log.log("Cannot find ? Mark in the input text: " + input, Log.LEVEL_ERROR);
    		return null;
    	}
    	
    	Map<String, String> result = new HashMap<String, String>();

    	StringTokenizer st = new StringTokenizer(input.substring(qMark), "&");
    	
    	while( st.hasMoreElements() )
    	{
    		String tmp = st.nextToken();
    		
    		qMark = tmp.indexOf("=");
    		
    		result.put(tmp.substring(0, qMark), tmp.substring(qMark, tmp.length()));
    	}
    	
    	return result;
    }
    
    public static String decode( final String input )
    {
    	if( null == input || input.isEmpty() )
    	{
    		return null;
    	}
    	
    	String result = null;
    	
    	try {
			result = URLDecoder.decode(input, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
	
    public static String encode( final String input )
    {
    	if( null == input || input.isEmpty() )
    	{
    		return null;
    	}
    	
    	String result = null;
    	
    	try {
			result = URLEncoder.encode(input, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
}
