package de.wagentim.webs.download;

import java.net.URI;
import java.util.List;

import de.wagentim.connect.PersisCookie;

public interface WebSite {
	
	/** Initial the login URI */
	URI getLoginURI();
	
	/** The entrance point of starting the job */
	void fetch();
	
	/** get in DB saved cookies, which are defined in previous response */
	List<PersisCookie> getSavedCookies(String identification);
}
