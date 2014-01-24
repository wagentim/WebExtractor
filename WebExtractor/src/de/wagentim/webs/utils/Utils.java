package de.wagentim.webs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public final class Utils {

	public static String handleCharCoding(final URL url, final String charCode) {

		InputStream inStr = null;

		if (null == url) {
			return null;
		}

		HttpURLConnection conn = null;

		try {

			conn = (HttpURLConnection) url.openConnection();

		} catch (IOException e) {

			conn = null;
		}

		if (null == conn) {

			return null;
		}

		HttpURLConnection.setFollowRedirects(true);

		StringBuffer buffer = new StringBuffer();

		String encoding = conn.getContentEncoding();

		try {
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				inStr = new GZIPInputStream(conn.getInputStream());
			} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
				inStr = new InflaterInputStream(conn.getInputStream(),
						new Inflater(true));
			} else {
				inStr = conn.getInputStream();
			}

			int ptr = 0;

			InputStreamReader inStrReader = new InputStreamReader(inStr,
					Charset.forName(charCode));

			while ((ptr = inStrReader.read()) != -1) {
				buffer.append((char) ptr);
			}
			inStrReader.close();

			conn.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStr != null)
			{
				try {
					inStr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return buffer.toString();

	}
	
	public static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }
    
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
    		
    		if( index < 0 )
    		{
    			unit = new BasicNameValuePair(pair.trim(), "");
    		}else
    		{
    			unit = new BasicNameValuePair(pair.substring(0, index).trim(), pair.substring(index + 1, pair.length()));
    		}
    		
    		result.add(unit);
    	}
    	
    	return result;
    }
	
}
