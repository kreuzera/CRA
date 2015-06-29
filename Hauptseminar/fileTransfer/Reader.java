package fileTransfer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import cra.model.Record;

public class Reader {
	
	/**
	 * Loads NodeList of Abstracts from specified file
	 * @param url file url of abstracts
	 * @return NodeList of Abstracts
	 */
	public ConcurrentLinkedQueue<Record> getAbstracts(File file){
		long start = System.currentTimeMillis();
		ConcurrentLinkedQueue<Record> nodeList = null;
		if(file.getName().endsWith(".xml"))
			nodeList = getAbstractsFromXml(file);
		if(file.getName().endsWith(".txt"))
			nodeList = getAbstractsFromTxt(file);
		//TODO implement other formats
		System.out.println("File read in "+(System.currentTimeMillis()-start)+"ms");
		return nodeList;
	}
	
	private ConcurrentLinkedQueue<Record> getAbstractsFromTxt(File txtFile){
		String content = null;
		   try {
		       FileReader reader = new FileReader(txtFile);
		       char[] chars = new char[(int) txtFile.length()];
		       reader.read(chars);
		       content = new String(chars);
		       reader.close();
		   } catch (IOException e) {
		       e.printStackTrace();
		   }
		ConcurrentLinkedQueue<Record> absList = new ConcurrentLinkedQueue<Record>();
		Record abs = new Record();
		abs.setAbstractText(content);
		absList.add(abs);

		return absList;
	}

	private ConcurrentLinkedQueue<Record> getAbstractsFromXml(File xmlFile){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
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
		NodeList recordList = doc.getElementsByTagName("rec");
		ConcurrentLinkedQueue<Record> finalList = new ConcurrentLinkedQueue<Record>();
		for(int i = 0; i<recordList.getLength(); i++){
			Node n = recordList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element ele = (Element) n;
				Record rec = new Record();
				NodeList tempList = ele.getElementsByTagName("ab");
				for(int j = 0; j<tempList.getLength(); j++){
					rec.setAbstractText(rec.getAbstractText()+tempList.item(j).getTextContent());
				}
				tempList = ele.getElementsByTagName("jtl");
				for(int j = 0; j<tempList.getLength(); j++){
					rec.setJournal(tempList.item(j).getTextContent());
				}
				tempList = ele.getElementsByTagName("atl");
				for(int j = 0; j<tempList.getLength(); j++){
					rec.setTitle(tempList.item(j).getTextContent());
				}
				tempList = ele.getElementsByTagName("au");
				for(int j = 0; j<tempList.getLength(); j++){
					rec.addAuthor(tempList.item(j).getTextContent());
				}
				tempList = ele.getElementsByTagName("url");
				for(int j = 0; j<tempList.getLength(); j++){
					rec.setUrl(tempList.item(j).getTextContent());
				}
				tempList = ele.getElementsByTagName("header");
				for(int j = 0; j<tempList.getLength(); j++){
					Element tempEle = (Element) tempList.item(j);
					rec.setUrlID(tempEle.getAttribute("uiTerm"));
					rec.setDb(tempEle.getAttribute("shortDbName"));
				}
				finalList.add(rec);
			}
		}
		return finalList;
	}
}
