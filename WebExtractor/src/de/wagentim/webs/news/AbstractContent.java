package de.wagentim.webs.news;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;

public abstract class AbstractContent implements Content {

	protected Document doc = null;

	public AbstractContent() {
		
		doc = getDoc();
	}

	protected abstract LogChannel getLogger();
	
	public Document getDoc()
	{
		Document d = null;
		
		try {
			d = Jsoup.connect(getURL()).get();
		} catch (IOException e) {
			getLogger().log(
					"Get Exception by parser Web content to Document object: " + getURL(),
					Log.LEVEL_CRITICAL_ERROR);
			d = null;
		}
		
		return d;
	}

}
