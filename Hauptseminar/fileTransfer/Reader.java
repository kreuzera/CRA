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

public class Reader {
	
	/**
	 * Loads NodeList of Abstracts from specified file
	 * @param url file url of abstracts
	 * @return NodeList of Abstracts
	 */
	public ConcurrentLinkedQueue<String> getAbstracts(File file){
		long start = System.currentTimeMillis();
		ConcurrentLinkedQueue<String> nodeList = null;
		if(file.getName().endsWith(".xml"))
			nodeList = getAbstractsFromXml(file);
		if(file.getName().endsWith(".txt"))
			nodeList = getAbstractsFromTxt(file);
		//TODO implement other formats
		System.out.println("File read in "+(System.currentTimeMillis()-start)+"ms");
		return nodeList;
	}
	
	private ConcurrentLinkedQueue<String> getAbstractsFromTxt(File txtFile){
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
		ConcurrentLinkedQueue<String> absList = new ConcurrentLinkedQueue<String>();
		absList.add(content);

		return absList;
	}

	private ConcurrentLinkedQueue<String> getAbstractsFromXml(File xmlFile){
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
