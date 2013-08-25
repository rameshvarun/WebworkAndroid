package com.varunramesh.webwork;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLUtils {

	public static void removeAllClass(Element element, String classname)
	{
		for(Element e : element.getElementsByClass(classname))
		{
			e.remove();
		}
	}
	
	public static void removeId(Element element, String id)
	{
		if(element.getElementById(id) != null)
    	{
    		element.getElementById(id).remove();
    	}
	}
	
	public static void removeAttributeValue(Element element, String attribute, String value)
	{
		for(Element e : element.getElementsByAttributeValue(attribute, value))
		{
			e.remove();
		}
	}
	
	public static void removeTag(Element element, String tag)
	{
		for(Element e : element.getElementsByTag(tag) )
		{
			e.remove();
		}
	}
	
	public static void removeTag(Element element, String tag, int index)
	{
		if(element.getElementsByTag(tag).size() > index)
		{
			element.getElementsByTag(tag).get(index).remove();
		}
	}
}
