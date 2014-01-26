package de.wagentim.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Cookie Persistence unit.
 * 
 * @author bihu8398
 *
 */
@Entity
public class PersisCookie {
	
	@Id
	@GeneratedValue
	private Long id = -1L;
	private String date = "";
	private String webName = "";
	private List<String> noValue = null;
	private String key = "";
	private String value = "";
	private String version = "";
	private String path = "";
	private String domain = "";
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public List<String> getNoValue() {
		return noValue;
	}

	public void setNoValue(List<String> noValue) {
		this.noValue = noValue;
	}

	public PersisCookie(String name, String value) {
		this.key = name;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}
	
	public void addNoValue(String name)
	{
		if( null == noValue )
		{
			noValue = new ArrayList<String>();
		}
		
		if( null != name && !name.isEmpty() )
		{
			noValue.add(name);
		}
	}
	
	@Override
	public String toString()
	{
		return getKey() + "=" + getValue();
	}

}
