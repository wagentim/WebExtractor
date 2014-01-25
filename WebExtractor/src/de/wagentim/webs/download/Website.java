package de.wagentim.webs.download;

import java.net.URI;

import org.apache.http.client.config.RequestConfig;

/**
 * Each Web Based Application/Complete Operation is a instance of Connection
 * 
 * @author wagentim
 *
 */
public interface Website extends Runnable{
	
	/**
	 * define local request configuration for the connection, such as {@link HttpGet}
	 * <br/>
	 * @return	Null is the default value, means no local request configuration is defined
	 */
	RequestConfig getLocalRequestConfig();
	
	/**
	 * Basic URI is the resource that the <code>Connection</code> can first connect to.
	 * 
	 * @return <code>URI</code>
	 */
	URI getBasicURI();
}
