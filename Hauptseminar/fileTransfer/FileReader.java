package fileTransfer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileReader {
	
	/**
	 * Loads NodeList of Abstracts from specified file
	 * @param url file url of abstracts
	 * @return NodeList of Abstracts
	 */
	public ConcurrentLinkedQueue<String> getAbstracts(String url){
		ConcurrentLinkedQueue<String> nodeList = null;
		if(url.endsWith(".xml"))
			nodeList = getAbstractsFromXml(url);
		//TODO implement other formats
		return nodeList;
	}
	
	private NodeList getFromXml(String folder){
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			File xmlFile = new File(folder);
			Document doc = null;
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(xmlFile);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			NodeList abstracts = doc.getElementsByTagName("ab");
			return abstracts;
	}
	
	private ConcurrentLinkedQueue<String> getAbstractsFromXml(String folder){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		File xmlFile = new File(folder);
		Document doc = null;
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList tempList = doc.getElementsByTagName("ab");
		HashMap<Node, String> abstractList = new HashMap<Node, String>();
		for(int i = 0; i<tempList.getLength(); i++){
			Node n = tempList.item(i);
			if(abstractList.get(n.getParentNode())!=null){
				abstractList.put(n.getParentNode(), abstractList.get(n.getParentNode())+n.getTextContent());
			}else{
				abstractList.put(n.getParentNode(), n.getTextContent());
			}			
		}
		ConcurrentLinkedQueue<String> finalList = new ConcurrentLinkedQueue<String>();
		for(String s: abstractList.values()){
			finalList.add(s);
		}
		
		return finalList;
}
}
