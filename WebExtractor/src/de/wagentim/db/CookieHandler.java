package de.wagentim.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.ClientCookie;

import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;
import de.wagentim.webs.utils.TextUtils;
import de.wagentim.webs.utils.Utils;

/**
 * Persistent Cookie in to "cookie.odb" and get <code>PersisCookie</code> from DB. It uses JPA Annotation to persistent the object
 * 
 * @author bihu8398
 *
 */
public class CookieHandler {
	
	public static final CookieHandler INSTANCE = new CookieHandler();
	private final EntityManagerFactory factory;
	private final EntityManager em;
	private static final String DB_NAME = "./db/cookies.odb";
	private final LogChannel log;
	private static final String KEY_COOKIE = "Set-Cookie";
	
	private CookieHandler()
	{
		factory = Persistence.createEntityManagerFactory(DB_NAME);
		em = factory.createEntityManager();
		int logID = QLoggerService.addChannel(new DefaultChannel("CookieHandler"));
		log = QLoggerService.getChannel(logID);
	}
	
	/**
	 * Parser Cookies from Response Header and pack then to <code>PersisCookie</code> object. As wishes, the Cookie can also be saved in DB
	 * 
	 * @param response
	 * @param webName
	 * @param toDB
	 * @return
	 */
	public List<PersisCookie> saveCookies(final HttpResponse response, String webName, boolean toDB)
	{
		if( null == response )
		{
			return null;
		}
		
		Header[] headers = response.getAllHeaders();
		
		if( null == headers || headers.length < 0 )
		{
			log.log(webName + ": Cannot get headers from the response!", Log.LEVEL_ERROR);
			return null;
		}
		
		List<PersisCookie> result = new ArrayList<PersisCookie>();
		
		for( Header h : headers )
		{
			if( KEY_COOKIE.equals(h.getName()))
			{
				result.add(saveCookies(h.getValue(), webName, toDB));
			}
		}
		
		if( !result.isEmpty() )
		{
			return result;
		}
		
		return null;
	}
	
	public PersisCookie saveCookies(String value, String webName, boolean toDB)
	{
		List<NameValuePair> result = Utils.parserCookie(value);
		
		if( result.isEmpty() )
		{
			return null;
		}
		
		int index = 0;
		
		PersisCookie cookie = null;
		
		for(NameValuePair nvp : result)
		{
			if( index == 0 )
			{
				cookie = new PersisCookie(nvp.getName(), nvp.getValue());
				cookie.setWebName(webName);
			}else
			{
				String name = nvp.getName();
				String val = nvp.getValue();
				
				if( name.equals(ClientCookie.VERSION_ATTR ))
				{
					cookie.setVersion(val);
				}else if( name.equals(ClientCookie.PATH_ATTR ))
				{
					cookie.setPath(val);
				}else if( name.equals(ClientCookie.DOMAIN_ATTR ))
				{
					cookie.setDomain(val);
				}else if( name.equals(ClientCookie.EXPIRES_ATTR ))
				{
					cookie.setDate(val);
				}else if( !name.isEmpty() && val.isEmpty() )
				{
					cookie.addNoValue(name);
				}
			}
			
			index++;
		}
		
		if( toDB )
		{
			em.getTransaction().begin();
			em.persist(cookie);
			em.getTransaction().commit();
		}
		
		return cookie;
	}
	
	public List<PersisCookie> getCookiesFromDB(final String identification)
	{
		if( null == identification || identification.isEmpty() )
		{
			return null;
		}
		
		TypedQuery<PersisCookie> query = em.createQuery( TextUtils.textReplace(QUERY_GET_COOKIES_FROM_DB_BY_USING_IDENTIFICATION, "WHERE webName=" + identification ), PersisCookie.class);
		
		return query.getResultList();
	}
	
	public void close()
	{
		em.close();
		factory.close();
	}
	
	private static final String QUERY_GET_COOKIES_FROM_DB_BY_USING_IDENTIFICATION = "SELECT cookie FROM PersisCookie cookie WHERE %1;";
	
}
