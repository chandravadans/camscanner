package com.necsv.scanner.esg.util;

public class Albums
{
	private String id;
	private String title;
	private String image;
	
	public Albums(String _title, String _image)
	{
		this.title 	= _title;
		this.image 	= _image;
	}
	
	public Albums(String _id, String _title, String _image)
	{		
		this.id		= _id;
		this.title 	= _title;
		this.image 	= _image;
	}
	
	public String getID()
	{
		return id;
	}

	public void setID(String _id)
	{
		this.id = _id;
	}
	
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String _title)
	{
		this.title = _title;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public void setImage(String _image)
	{
		this.image = _image;
	}
}
