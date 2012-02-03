package com.necsv.scanner.esg.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import android.content.Context;
import android.util.Log;

public class XMLUtil
{
	private static Document doc;
	private static DocumentBuilder db;
	private static DocumentBuilderFactory dbf;
	
	//Parser data from file AlbumsList XML into array List
	public static List<Albums> parserAlbumsListData(Context mContext)
	{
		try
		{
			File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);
		    doc.getDocumentElement().normalize();
		    NodeList nodeLst = doc.getElementsByTagName("items");
			List<Albums> mAlbums = new ArrayList<Albums>();
			for (int i = 0; i < nodeLst.getLength(); i++)
		    {
	      	    Node note = nodeLst.item(i);      	    
	      	    if (note.getNodeType() == Node.ELEMENT_NODE)
	      	    {
	      	    	//ID
	      	    	Element idElement 		= (Element) note;
	      	    	NodeList noteID 		= idElement.getElementsByTagName("id");
	      	    	Element ID 				= (Element) noteID.item(0);
	      	    	NodeList nodelistID 	= ID.getChildNodes();
	      	    	//Title
	      	    	Element titleElement 	= (Element) note;
	      	    	NodeList noteTitle 		= titleElement.getElementsByTagName("title");
	      	    	Element title 			= (Element) noteTitle.item(0);
	      	    	NodeList nodelistTitle 	= title.getChildNodes();
	      	        //Image
//	      	        Element imagesElement 	= (Element) note;
//	    	    	NodeList noteImage 		= imagesElement.getElementsByTagName("image");
//	    	    	Element image 			= (Element) noteImage.item(0);
//	    	    	NodeList nodelistImage 	= image.getChildNodes();
	    	        
	    	    	String valueID 		= ((Node) nodelistID.item(0)).getNodeValue();
	    	        String valueTitle 	= ((Node) nodelistTitle.item(0)).getNodeValue();
//	    	        String imagesName 	= ((Node) nodelistImage.item(0)).getNodeValue();
	    	        
	    	        mAlbums.add(new Albums(valueID, valueTitle, null));
	      	    }
		    }	
			return mAlbums;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static List<Albums> parserAlbumsGridData(Context mContext, String filename)
	{
		try
		{
			File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/" + filename + ".xml");

			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);		    
		    doc.getDocumentElement().normalize();
		    NodeList nodeLst = doc.getElementsByTagName("items");
			List<Albums> mAlbums = new ArrayList<Albums>();
			for (int i = 0; i < nodeLst.getLength(); i++)
		    {
	      	    Node note = nodeLst.item(i);      	    
	      	    if (note.getNodeType() == Node.ELEMENT_NODE)
	      	    {
	      	    	//Title
	      	    	Element titleElement 	= (Element) note;
	      	    	NodeList noteTitle 		= titleElement.getElementsByTagName("title");
	      	    	Element title 			= (Element) noteTitle.item(0);
	      	    	NodeList nodelistTitle 	= title.getChildNodes();
	      	        //Image
	      	        Element imagesElement 	= (Element) note;
	    	    	NodeList noteImage 		= imagesElement.getElementsByTagName("image");
	    	    	Element image 			= (Element) noteImage.item(0);
	    	    	NodeList nodelistImage 	= image.getChildNodes();
	    	        
	    	        String valueTitle 	= ((Node) nodelistTitle.item(0)).getNodeValue();
	    	        String imagesName 	= ((Node) nodelistImage.item(0)).getNodeValue();
	    	        
	    	        mAlbums.add(new Albums(valueTitle, imagesName));
	      	    }
		    }	
			return mAlbums;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static String CreateNewAlbumListData(Context mContext, String picture_name, String album_name)
	{
		int count = 0;
		String path;
		// Add new item in albumlist.xml
		try {	
			File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);
		    doc.getDocumentElement().normalize();

	        // Root Element
	        Element rootElement = doc.getDocumentElement();	
	        Element a = (Element)rootElement;
		    Element items = doc.createElement("items");

		    NodeList nodeLst = doc.getElementsByTagName("items");
		    count = nodeLst.getLength() + 1;
		    
            Element id = doc.createElement("id");
            id.appendChild(doc.createTextNode(String.valueOf(count)));
            items.appendChild(id);

            Element title = doc.createElement("title");
            if (album_name == "") {
            	title.appendChild(doc.createTextNode("album" + String.valueOf(count)));
            } else {
            	title.appendChild(doc.createTextNode(album_name));
            }
            items.appendChild(title);
            
            a.appendChild(items);

            DOMSource source = new DOMSource(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
            transformer.transform(source, result);

		} catch (Exception e) {
			return null;
		}	
		
		// Add new folder + image for new item 
		File newxmlfile = new File(GlobalVariable.TARGET_PATH + "xml/" + String.valueOf(count) + ".xml");
		File newfolder = new File(GlobalVariable.TARGET_PATH + "images/" + String.valueOf(count));
		try {
            newxmlfile.createNewFile();
            newfolder.mkdirs(); 
        } catch (IOException e) {
            Log.e("IOException", "Exception in create new File(");
            return null;
        }
        try
		{			    
			
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.newDocument();
		   // doc.getDocumentElement().normalize();
			// root elements
			Element rootElement = doc.createElement("album");
			doc.appendChild(rootElement);
	 
			// items elements
			Element items = doc.createElement("items");
			rootElement.appendChild(items);
	 
			// title elements
			Element title = doc.createElement("title");
			title.appendChild(doc.createTextNode("1"));
			items.appendChild(title);
	 
			// image elements
			Element image = doc.createElement("image");
            
            if (picture_name == "") {
            	path = "pic1.jpg";
            	image.appendChild(doc.createTextNode(path));
                
            } else {
            	path = picture_name + ".jpg";
            	image.appendChild(doc.createTextNode(path));
            	
            }
            items.appendChild(image);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(newxmlfile);
			transformer.transform(source, result);

		} catch (Exception e) {
			return null;
		}
		return GlobalVariable.TARGET_PATH + "images/" + String.valueOf(count) + "/" + path;
	}
	
	public static boolean CreateNewAlbum(Context mContext, String name)
	{
		int count = 0;
		try
		{			    
			File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);
		    doc.getDocumentElement().normalize();

	        // Root Element
	        Element rootElement = doc.getDocumentElement();	
	        Element a = (Element)rootElement;
		    Element items = doc.createElement("items");

		    NodeList nodeLst = doc.getElementsByTagName("items");
		    count = nodeLst.getLength() + 1;
		    
            Element id = doc.createElement("id");
            id.appendChild(doc.createTextNode(String.valueOf(count)));
            items.appendChild(id);

            Element title = doc.createElement("title");
            if (name == "") {
            	title.appendChild(doc.createTextNode("album" + String.valueOf(count)));
            } else {
            	title.appendChild(doc.createTextNode(name));
            }
            items.appendChild(title);
            
            a.appendChild(items);

            DOMSource source = new DOMSource(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
            transformer.transform(source, result);
            
		}
		catch (Exception e)
		{
			return false;
		}
		
		// Add new folder + image for new item 
		File newxmlfile = new File(GlobalVariable.TARGET_PATH + "xml/" + String.valueOf(count) + ".xml");
		File newfolder = new File(GlobalVariable.TARGET_PATH + "images/" + String.valueOf(count));
		try {
            newxmlfile.createNewFile();
            newfolder.mkdirs(); 
        } catch (IOException e) {
            Log.e("IOException", "Exception in create new File(");
        }
        try
		{			    	
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.newDocument();
			// root elements
			Element rootElement = doc.createElement("album");
			doc.appendChild(rootElement);
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(newxmlfile);
			transformer.transform(source, result);

		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String CreateNewPicture(Context mContext, String picture_name, String album_name)
	{
		int count = 0;
		String ID = ""; 
		String path = "";
		List<Albums> mAlbums = new ArrayList<Albums>();
		mAlbums	  = XMLUtil.parserAlbumsListData(mContext);
		for (int i = 0; i < mAlbums.size(); i++) {
			if (mAlbums.get(i).getTitle().compareTo(album_name) == 0) {
				ID = mAlbums.get(i).getID();
				break;
			}	
		}
		if (ID == "") return ""; 
		// Add new folder + image for new item 
		File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/" + ID + ".xml");
        try
		{			    		
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);
		    doc.getDocumentElement().normalize();
		    
	        // Root Element
	        Element rootElement = doc.getDocumentElement();	
	        Element a = (Element)rootElement;
		    Element items = doc.createElement("items");

		    NodeList nodeLst = doc.getElementsByTagName("items");
		    count = nodeLst.getLength() + 1;
	 
			// title elements
			Element title = doc.createElement("title");
			title.appendChild(doc.createTextNode(String.valueOf(count)));
			items.appendChild(title);
	 
			// image elements
			Element image = doc.createElement("image");

            if (picture_name == "") {
            	path = "pic" + count + ".jpg";
            	image.appendChild(doc.createTextNode(path));
            } else {
            	path = picture_name + ".jpg";
            	image.appendChild(doc.createTextNode(path));
            }
            items.appendChild(image);
            a.appendChild(items);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);

		} catch (Exception e) {
			return null;
		}
		return GlobalVariable.TARGET_PATH + "images/" + ID + "/" + path;
	}
	
	
	public static boolean DeleteAlbum(Context mContext, int item)
	{
		try
		{			    
			File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);
		    doc.getDocumentElement().normalize();

	        // Root Element
	        Element rootElement = doc.getDocumentElement();	
	        Element a = (Element)rootElement;

		    NodeList nodeLst = doc.getElementsByTagName("items");
		    a.removeChild(nodeLst.item(item));

            DOMSource source = new DOMSource(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(GlobalVariable.TARGET_PATH + "xml/albumslist.xml");
            transformer.transform(source, result);
            
            FileUtil.deleteFile(GlobalVariable.TARGET_PATH + "images/" + String.valueOf(item + 1));
            FileUtil.deleteFile(GlobalVariable.TARGET_PATH + "xml/" + String.valueOf(item + 1) + ".xml");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean DeletePicture(Context mContext, int itemAlbum, int itemPicture) {
		try
		{			    
			File xmlFile = new File(GlobalVariable.TARGET_PATH + "xml/" + String.valueOf(itemAlbum + 1) + ".xml");
			dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    doc = db.parse(xmlFile);
		    doc.getDocumentElement().normalize();

	        // Root Element
	        Element rootElement = doc.getDocumentElement();	
	        Element a = (Element)rootElement;

		    NodeList nodeLst = doc.getElementsByTagName("items");
		    String imagesName = "";
      	    Node note = nodeLst.item(itemPicture);      	    
  	        //Image
  	        Element imagesElement 	= (Element) note;
	    	NodeList noteImage 		= imagesElement.getElementsByTagName("image");
	    	Element image 			= (Element) noteImage.item(0);
	    	NodeList nodelistImage 	= image.getChildNodes();
	        
	        imagesName 	= ((Node) nodelistImage.item(0)).getNodeValue();

		    a.removeChild(nodeLst.item(itemPicture));

            DOMSource source = new DOMSource(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(GlobalVariable.TARGET_PATH + "xml/" + String.valueOf(itemAlbum + 1) + ".xml");
            transformer.transform(source, result);
            FileUtil.deleteFile(GlobalVariable.TARGET_PATH + "images/" + String.valueOf(itemAlbum + 1) + "/" + imagesName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
