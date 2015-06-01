package fileTransfer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileReader {
	
	/**
	 * Loads NodeList of Abstracts from specified file
	 * @param url file url of abstracts
	 * @return NodeList of Abstracts
	 */
	public NodeList getAbstracts(String url){
		NodeList nodeList = null;
		if(url.endsWith(".xml"))
			nodeList = getFromXml(url);
		//TODO implement other formats
		return nodeList;
	}
	
	private NodeList getFromXml(String folder){
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			File xmlFile = new File("C:/Users/Kai/Downloads/7a5203ab-9943-4adf-a603-6cf625331bdf/ebscohost export.xml");
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
}
