package de.wagentim.db.handler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.http.NameValuePair;
import org.apache.http.cookie.ClientCookie;

import de.wagentim.connect.PersisCookie;
import de.wagentim.webs.utils.TextUtils;
import de.wagentim.webs.utils.Utils;

/**
 * Persistent Cookie in to "cookie.odb" and get <code>PersisCookie</code> from DB. It uses JPA Annotation to persistent the object
 * 
 * @author bihu8398
 *
 */
public class CookieHandler {
	
	private static EntityManagerFactory factory;
	private static EntityManager em;
	private static final String DB_NAME = "./db/cookies.odb";
	
	static{
		
		factory = Persistence.createEntityManagerFactory(DB_NAME);
		em = factory.createEntityManager();
	}
	
	public static PersisCookie saveCookiesToDB(String value, String webName)
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
				String name = nvp.getName().toLowerCase();
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
		
		em.getTransaction().begin();
		em.persist(cookie);
		em.getTransaction().commit();
		
		return cookie;
	}
	
	public static List<PersisCookie> getCookiesFromDB(final String identification)
	{
		if( null == identification || identification.isEmpty() )
		{
			return null;
		}
		
		TypedQuery<PersisCookie> query = em.createQuery( TextUtils.textReplace(QUERY_GET_COOKIES_FROM_DB_BY_USING_IDENTIFICATION, "WHERE webName=" + identification ), PersisCookie.class);
		
		return query.getResultList();
	}
	
	public static void close()
	{
		em.close();
		factory.close();
	}
	
	private static final String QUERY_GET_COOKIES_FROM_DB_BY_USING_IDENTIFICATION = "SELECT cookie FROM PersisCookie cookie WHERE %1;";
	
}
