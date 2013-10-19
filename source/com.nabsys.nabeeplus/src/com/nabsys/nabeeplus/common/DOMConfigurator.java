package com.nabsys.nabeeplus.common;

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

import com.nabsys.nabeeplus.common.label.NBLabel;

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
	
	//key 의 마지막 노드에서만 ID 체크
	public void setConf(String key, String id, HashMap<String, String> childNodesMap) throws Exception
	{
		String keyList[] = key.split("/");
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			doc.appendChild(doc.createElement(keyList[0]));
			element = doc.getDocumentElement();
		}
		
		Element last = null;
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);
			
			//id 체크는 마지막 키 체인에서만 체크하는 펑션
			if(i == keyList.length - 1)
			{
				for(int j=0; j<nodeList.getLength() && nodeList.item(j).getNodeType() == Node.ELEMENT_NODE; j++)
				{
					if(nodeList.item(j).getAttributes().getNamedItem("id").getTextContent().equals(id))
					{
						throw new Exception(NBLabel.get(0x008D));
					}
				}
				last = doc.createElement(keyList[i]);
				element.appendChild(last);
				break;
			}
			
			Element tmpElement = (Element)nodeList.item(0);
			if(tmpElement == null)
			{
				element.appendChild(doc.createElement(keyList[i]));
				element.appendChild(doc.createTextNode("\n"));
				nodeList = element.getElementsByTagName(keyList[i]);
			}
			element = (Element)nodeList.item(0);
		}
		
		last.setAttribute("id", id);
		
		Set<String> keySet = childNodesMap.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String mapKey = itr.next();
			Element entity = doc.createElement(mapKey);
			entity.setTextContent(childNodesMap.get(mapKey));
			last.appendChild(entity);
			last.appendChild(doc.createTextNode("\n"));
		}
		
		save();
	}
	
	//key 의 마지막 노드에서만 ID 체크
	public void deleteConf(String key, String id) throws Exception
	{
		String keyList[] = key.split("/");
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			return;
		}

		boolean isExist = false;
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getChildNodes();

			//id 체크는 마지막 키 체인에서만 체크하는 펑션
			if(i == keyList.length - 1)
			{

				for(int j=0; j<nodeList.getLength(); j++)
				{
					if(nodeList.item(j).getNodeType() == Node.ELEMENT_NODE &&
							nodeList.item(j).getAttributes().getNamedItem("id").getTextContent().equals(id))
					{
						//DELETE
						Node parent = nodeList.item(j).getParentNode();
						
						parent.removeChild(nodeList.item(j));
						parent.removeChild(nodeList.item(j-1));
						
						save();
						return;
					}
				}
				if(!isExist)
					throw new Exception(NBLabel.get(0x008E));

			}
			
			element = (Element)element.getElementsByTagName(keyList[i]).item(0);
		}
	}
	
	//key 의 마지막 노드에서만 ID 체크
	public void modifyConf(String key, String id, HashMap<String, String> childNodesMap) throws Exception
	{
		String keyList[] = key.split("/");
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			return;
		}

		boolean isExist = false;
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);

			//id 체크는 마지막 키 체인에서만 체크하는 펑션
			if(i == keyList.length - 1)
			{

				for(int j=0; j<nodeList.getLength(); j++)
				{
					Node node = nodeList.item(j);
					if(node.getNodeType() == Node.ELEMENT_NODE &&
							node.getAttributes().getNamedItem("id").getTextContent().equals(id))
					{
						Set<String> keySet = childNodesMap.keySet();
						Iterator<String> itr = keySet.iterator();
						while(itr.hasNext())
						{
							String mapKey = itr.next();
							for(int k=0; k<node.getChildNodes().getLength(); k++)
							{
								Node entity = node.getChildNodes().item(k);
								if(entity.getNodeType() == Node.ELEMENT_NODE && entity.getNodeName().equals(mapKey))
									entity.setTextContent(childNodesMap.get(mapKey));
							}
						}
						isExist = true;
						save();
						return;
					}
				}
				if(!isExist)
					throw new Exception(NBLabel.get(0x008E));

			}
			
			element = (Element)nodeList.item(0);
		}
	}
	
	//key 의 마지막 노드에서만 ID 체크
	public void modifyConf(String key, String oldId, String newId) throws Exception
	{
		String keyList[] = key.split("/");
		
		NodeList nodeList = doc.getElementsByTagName(keyList[0]);
		Element element = (Element)nodeList.item(0);

		if(element == null)
		{
			return;
		}

		boolean isExist = false;
		for(int i=1; i<keyList.length; i++)
		{
			nodeList = element.getElementsByTagName(keyList[i]);

			//id 체크는 마지막 키 체인에서만 체크하는 펑션
			if(i == keyList.length - 1)
			{

				for(int j=0; j<nodeList.getLength(); j++)
				{
					if(nodeList.item(j).getNodeType() == Node.ELEMENT_NODE &&
							nodeList.item(j).getAttributes().getNamedItem("id").getTextContent().equals(oldId))
					{
						nodeList.item(j).getAttributes().getNamedItem("id").setTextContent(newId);
						save();
						return;
					}
				}
				if(!isExist)
					throw new Exception(NBLabel.get(0x008E));

			}
			
			element = (Element)nodeList.item(0);
		}
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
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public HashMap<String, String> getSubNodeMapByNodeID(String key, String id)
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
								map.put(element.getNodeName(), tx==null?"":tx.getData());
							}
						}
					}
				}
			}
			
		}catch(Exception e){
		}
		
		return map;
	}
	
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
	
	public NodeList getSubNodeList(String key)
	{
		NodeList nodeList = null;
		try{
			String keyList[] = key.split("/");
			
			Element element = moveToElement(keyList);
			
			nodeList = element.getChildNodes();

		}catch(Exception e){
			e.printStackTrace();
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

}
