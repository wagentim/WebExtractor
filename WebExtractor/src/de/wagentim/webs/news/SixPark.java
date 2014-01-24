package de.wagentim.webs.news;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.wagentim.elements.News;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;
import de.wagentim.webs.utils.Constants;


public class SixPark extends AbstractContent {
	
	private static int id = QLoggerService.addChannel(new DefaultChannel("6park"));
	private final String DEFALUT_URL = "http://news.6park.com/newspark/"; 
	private String url = DEFALUT_URL;
	private final String DIV_D_LIST = "d_list";
	private final String LI = "li";
	
	private LogChannel log = getLogger();
	
	public SixPark()
	{
		super();
		extractNews();
	}
	
	private List<News> extractNews() 
	{
		
		if( null == doc )
		{
			log.log("Parser Web Root is NULL", Log.LEVEL_CRITICAL_ERROR);
			return null;
		}
		
		Element newsBlock = doc.getElementById(DIV_D_LIST);
		
		if( null == newsBlock )
		{
			log.log(String.format("%s: %s", "Cannot find news block", DIV_D_LIST), Log.LEVEL_CRITICAL_ERROR);
			return null;
		}
		
		Elements items = newsBlock.getElementsByTag(LI);
		
		if( null == items || items.size() <= 0 )
		{
			log.log(String.format("%s", "No Item in the list"), Log.LEVEL_WARN);
			return null;
		}
		
		List<News> results = new ArrayList<News>();
		
		for( Element e : items )
		{
			Elements i = e.select("a[href]");
			
			if( null != i )
			{
				Element link = i.get(0);
				
				News n = new News();
				
				n.setTitle(link.text());
				System.out.println(n.getTitle());
				
				getContent(link.attr("href"), n);
				
				results.add(n);
			}
		}
		
		return results;
	}

	private void getContent(String attr, News n) {
		
		setURL(DEFALUT_URL + attr );
		
		doc = getDoc();
		
		// find main content block
		Elements body = doc.getElementsByTag(Constants.TAG_P);
		
		if( body.size() <= 0 )
		{
			log.log("No P block find!", Log.LEVEL_INFO);
			
			return;
		}
		
		if( body.size() > 1 )
		{
			log.log("There are more than one P block", Log.LEVEL_WARN);
		}
		
		parserPBlock(body.get(0), n);
		
	}


	private void parserPBlock(Element element, News n) {

		Elements eles = element.getElementsByTag(Constants.TAG_DIV);
		
		if( eles.size() > 0 )
		{
			parserDivBlock(eles, n);
		}
		
	}

	private void parserDivBlock(Elements eles, News n) {
		
		
	}

	private Image getImage(String attr) {
		
		return null;
	}

	@Override
	public String getURL() {
		
		return url;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}

	@Override
	protected LogChannel getLogger() {
		
		return QLoggerService.getChannel(id);
	}
}
