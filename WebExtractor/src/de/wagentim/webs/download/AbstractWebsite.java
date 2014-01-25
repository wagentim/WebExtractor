package de.wagentim.webs.download;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.service.QLoggerService;


public abstract class AbstractWebsite implements Website {
	
	private String webName = "NO_NAME";
	
	private int logID = -1;
	
	protected LogChannel log = null;
	
	public AbstractWebsite(final String name)
	{
		if( null != name && !name.isEmpty() )
		{
			this.webName = name;
		}
		
		logID = QLoggerService.addChannel(new DefaultChannel(null == name ? "" : name));
		log = getLog();
	}
	
	protected LogChannel getLog()
	{
		return logID < 0 ? null : QLoggerService.getChannel(logID);
	}
	
	@Override
	public RequestConfig getLocalRequestConfig()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(webName);
		sb.append(": ");
		sb.append(null == getBasicURI() ? "" : getBasicURI().toString());
		return sb.toString();
	}
	
	public void addRequestHeaders(HttpRequestBase request)
	{
		// Do Nothing here. The detail header should be implemented by sub-class
	}
	
}
