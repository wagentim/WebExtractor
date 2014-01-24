package de.wagentim.webs.download;

import java.util.List;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.util.Args;

import de.wagentim.connect.PersisCookie;
import de.wagentim.db.handler.CookieHandler;

public abstract class AbstractWebSite implements WebSite {
	
	
	public List<PersisCookie> getSavedCookies(String identification)
	{
		String original = Args.notEmpty( Args.notNull(identification, "AbstractWebSite#getSavedCookies identification is null"), "AbstractWebSite#getSavedCookies identification is empty");
		
		return CookieHandler.getCookiesFromDB(original);
	}
	
	public RequestConfig getLocalRequestConfig()
	{
		return null;
	}
}
