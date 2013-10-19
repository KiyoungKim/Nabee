package com.nabsys.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DOMConfigurator {
	private Document doc = null;
	private File docFile = null;
	private boolean newGenerated = false;
	
	public DOMConfigurator(String path) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		docFile = new File(path);
		
		if(!docFile.exists())
		{
			genXML();
			newGenerated = true;
		}
		else
		{
		
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			doc = db.parse(docFile);
			
			doc.getDocumentElement().normalize();
		}
	}
	
	public boolean newGenerated()
	{
		return this.newGenerated;
	}
	
	public String getConf(String key)
	{
		String value = "";
		
		try{
			String keyList[] = key.split("/");
			
			Element element = moveToElement(keyList);
			
			Text tx = (Text)element.getFirstChild();
			
			value = tx==null?"":tx.getData();
			if(value == null) value = "";
		}catch(Exception e){
			value = "";
		}
		
		return value;
	}
	
	public HashMap<String, String> getParams(String key)
	{
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		
		try{
			String keyList[] = key.split("/");
			
			Element element = moveToElement(keyList);

			NamedNodeMap clientNodeMap = element.getAttributes();
			for(int i=0; i<clientNodeMap.getLength(); i++)
			{
				rtnMap.put(clientNodeMap.item(i).getNodeName(), clientNodeMap.item(i).getNodeValue());
			}

		}catch(Exception e){
		}
		
		return rtnMap;
	}
	
	public void setConf(String key, String value) throws TransformerException
	{

		String keyList[] = key.split("/");
			
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			doc.appendChild(doc.createElement(keyList[0]));
			element = doc.getDocumentElement();
		}
		
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);
			Element tmpElement = (Element)nodeList.item(0);
			
			if(tmpElement == null)
			{
				element.appendChild(doc.createElement(keyList[i]));
				element.appendChild(doc.createTextNode("\n"));
				nodeList = element.getElementsByTagName(keyList[i]);
			}
			
			element = (Element)nodeList.item(0);
		}
		
		element.setTextContent(value);
		
		save();
	}
	
	public void setParams(String key, HashMap<String, String> valueMap) throws TransformerException
	{
		String keyList[] = key.split("/");
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			doc.appendChild(doc.createElement(keyList[0]));
			element = doc.getDocumentElement();
		}
		
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);
			Element tmpElement = (Element)nodeList.item(0);
			if(tmpElement == null)
			{
				element.appendChild(doc.createElement(keyList[i]));
				element.appendChild(doc.createTextNode("\n"));
				nodeList = element.getElementsByTagName(keyList[i]);
			}
			element = (Element)nodeList.item(0);
		}
		
		Set<String> set = valueMap.keySet();
		Iterator<String> itr = set.iterator();
		while(itr.hasNext())
		{
			String mapKey = itr.next();
			element.setAttribute(mapKey, valueMap.get(mapKey));
		}
		
		save();
	}

	//프로토콜, 플러그인 등록
	public void setParamByID(String key, String id, String field, String value) throws TransformerException
	{
		String keyList[] = key.split("/");
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			doc.appendChild(doc.createElement(keyList[0]));
			element = doc.getDocumentElement();
		}
		
		for(int i=1; i<keyList.length - 1; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);
			Element tmpElement = (Element)nodeList.item(0);
			if(tmpElement == null)
			{
				element.appendChild(doc.createElement(keyList[i]));
				element.appendChild(doc.createTextNode("\n"));
				nodeList = element.getElementsByTagName(keyList[i]);
			}
			element = (Element)nodeList.item(0);
		}
		
		
		nodeList = element.getChildNodes();
		boolean isExist = false;
		for(int i=0; i<nodeList.getLength(); i++)
		{
			if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				if(nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue().equals(id))
				{
					((Element)nodeList.item(i)).setAttribute(field, value);
					isExist = true;
					break;
				}
			}
		}
		
		if(!isExist)
		{
			Element newElement = doc.createElement(keyList[keyList.length -1]);
			newElement.setAttribute(field, value);
			element.appendChild(newElement);
			element.appendChild(doc.createTextNode("\n"));
		}
		
		save();
	}
	
	public void removeByID(String key, String id) throws TransformerException
	{
		try{
			NodeList nodeList = getSubNodeList(key);

			for(int i=0; i<nodeList.getLength(); i++)
			{
				if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					if(nodeList.item(i).getAttributes().getNamedItem("id").getTextContent().equals(id))
					{
						Node parent = nodeList.item(i).getParentNode();
						
						parent.removeChild(nodeList.item(i));
						parent.removeChild(nodeList.item(i-1));
					}
				}
			}
			
		}catch(Exception e){
		}
		save();
	}

	public ArrayList<String> getSubNodeIDList(String key)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try{
			NodeList nodeList = getSubNodeList(key);

			for(int i=0; i<nodeList.getLength(); i++)
			{
				if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element)nodeList.item(i);
					list.add(element.getAttribute("id"));
				}
			}
			
		}catch(Exception e){
		}
		
		return list;
	}
	
	//프로토콜 파라메터 구하는 메소드
	public HashMap<String, String> getSubNodeListMapBySubNodeIDList(String key, String id)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		try{
			NodeList nodeList = getSubNodeList(key);

			for(int i=0; i<nodeList.getLength(); i++)
			{
				if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element)nodeList.item(i);
					
					if(element.getAttribute("id").equals(id))
					{
						NodeList subList = element.getChildNodes();
						for(int j=0; j<subList.getLength(); j++)
						{
							if(subList.item(j).getNodeType() == Node.ELEMENT_NODE)
							{
								element = (Element)subList.item(j);
								Text tx = (Text)element.getFirstChild();
								map.put(element.getAttribute("id"), tx==null?"":tx.getData());
							}
						}
					}
				}
			}
			
		}catch(Exception e){
		}
		
		return map;
	}

	//프로토콜 정보 구하는 방법
	public ArrayList<HashMap<String, String>> getSubNodeListParamMap(String key)
	{
		ArrayList<HashMap<String, String>> rtnList = new ArrayList<HashMap<String, String>>();
		
		try{
			NodeList nodeList = getSubNodeList(key);

			for(int i=0; i<nodeList.getLength(); i++)
			{
				if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element)nodeList.item(i);
					
					NamedNodeMap clientNodeMap = element.getAttributes();
					HashMap<String, String> map = new HashMap<String, String>();
					for(int j=0; j<clientNodeMap.getLength(); j++)
					{
						map.put(clientNodeMap.item(j).getNodeName(), clientNodeMap.item(j).getNodeValue());
					}
					
					rtnList.add(map);
				}
			}
			
		}catch(Exception e){
		}
		
		return rtnList;
	}
	
	public NodeList getSubNodeList(String key)
	{
		NodeList nodeList = null;
		try{
			String keyList[] = key.split("/");
			
			Element element = moveToElement(keyList);
			
			nodeList = element.getChildNodes();

		}catch(Exception e){
		}
		
		return nodeList;
	}
	
	private Element moveToElement(String keyList[])
	{
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);

		Element element = (Element)nodeList.item(0);
		
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);
			element = (Element)nodeList.item(0);
		}
		
		return element;
	}
	
	private void genXML() throws TransformerException, ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		doc = db.newDocument();
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		
		DOMSource source = new DOMSource(doc);
		StreamResult targetFile = new StreamResult(docFile);
		transformer.transform(source, targetFile);
	}
	
	private synchronized void save() throws TransformerException
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		
		DOMSource source = new DOMSource(doc);
		StreamResult targetFile = new StreamResult(docFile);
		transformer.transform(source, targetFile);
	}
	
	public void setProtocolParam(String key, String pid, String id, String field, String value) throws TransformerException
	{
		try{
			String keyList[] = key.split("/");
			NodeList nodeList = doc.getElementsByTagName(keyList[0]);
			
			Element element = (Element)nodeList.item(0);
			nodeList = element.getElementsByTagName(keyList[1]);
			
			element = (Element)nodeList.item(0);
			nodeList = element.getElementsByTagName(keyList[2]);
			
			boolean isExist = false;
			Element paramNodeElement = null;
			for(int i=0; i<nodeList.getLength(); i++)
			{
				element = (Element) nodeList.item(i);
				
				if(element.getAttribute("id").equals(pid))
				{
					nodeList = element.getElementsByTagName(keyList[3]);
					isExist = true;
					paramNodeElement = element;
					break;
				}
			}
			
			if(isExist)
			{
				isExist = false;
				for(int i=0; i<nodeList.getLength(); i++)
				{
					element = (Element) nodeList.item(i);
					if(element.getAttribute("id").equals(id))
					{
						if(field.equals("ID"))
						{
							element.setAttribute("id", value);
						}
						else if(field.equals("VALUE"))
						{
							element.setTextContent(value);
						}
						
						isExist = true;
						break;
					}
				}
				
				if(!isExist)
				{
					Element childNode = doc.createElement("param");
					if(field.equals("ID"))
					{
						childNode.setAttribute("id", value);
					}
					else
					{
						childNode.setTextContent(value);
					}
					
					paramNodeElement.appendChild(childNode);
					paramNodeElement.appendChild(doc.createTextNode("\n"));

					
				}
			}
		}catch(Exception e){

		}
		
		save();
		
	}
	
	public void removeProtocolParam(String key, String pid, String id) throws TransformerException
	{
		String keyList[] = key.split("/");
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		
		Element element = (Element)nodeList.item(0);
		nodeList = element.getElementsByTagName(keyList[1]);
		
		element = (Element)nodeList.item(0);
		nodeList = element.getElementsByTagName(keyList[2]);
		

		boolean isExist = false;
		for(int i=0; i<nodeList.getLength(); i++)
		{
			element = (Element) nodeList.item(i);
			
			if(element.getAttribute("id").equals(pid))
			{
				nodeList = element.getChildNodes();
				isExist = true;
				break;
			}
		}
		
		for(int i=0; i<nodeList.getLength() && isExist; i++)
		{
			if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				element = (Element) nodeList.item(i);
				if(element.getAttribute("id").equals(id))
				{
					Node parent = nodeList.item(i).getParentNode();
					
					parent.removeChild(nodeList.item(i));
					parent.removeChild(nodeList.item(i-1));
				}
			}
		}
		
		save();

	}

}
