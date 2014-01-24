package de.wagentim.elements;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class News {
	
	private String title = "";
	private String content = "";
	private Long time = -1L;
	private List<Image> images = null;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	
	public void addImage( Image image )
	{
		if( null == images )
		{
			images = new ArrayList<Image>();
		}
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
}
