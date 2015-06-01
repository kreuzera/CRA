package craMain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import model.Element;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fileTransfer.FileReader;
import filtering.NPFilter;

public class CRAMain {

	public static void main(String[] args) {
		FileReader fileReader = new FileReader();
		NodeList abstracts = fileReader.getAbstracts("testFile/ebscohost export.xml");
		
		NPFilter npfilter = new NPFilter();
		
		ArrayList<String[]> taggedWordList = new ArrayList<String []>();
		LinkedList<Element> nounPhrases = new LinkedList<Element>();
		LinkedList<Element> lastElements = new LinkedList<Element>();
		Element tempElement = null;
		for (int i = 0; i<abstracts.getLength(); i++){
//			System.out.println(abstracts.item(i).getTextContent());
			taggedWordList = npfilter.GetTaggedWordsFromSentence(abstracts.item(i).getTextContent());
			
			for(String[] s : taggedWordList){
				if(s[0].equals("NN")||s[0].equals("NNS")||s[0].equals("NNP")||s[0].equals("JJ")||s[0].equals("JJR")||s[0].equals("JJS")){
					lastElements.add(new Element(s[1]));
				}else{
					if(!lastElements.isEmpty()){
						if(tempElement!=null){
							lastElements.getFirst().addNeighbour(tempElement);
							tempElement.addNeighbour(lastElements.getFirst());
						}
						for(Element e: lastElements){
							for(Element n: lastElements){
								e.addNeighbour(n);								
							}							
							e.getNeighbour().remove(e);
							nounPhrases.add(e);
						}

						tempElement = lastElements.getLast();
						lastElements.clear();
					}
				}
				if(s[0].equals(".")){
					tempElement=null;
					lastElements.clear();
				}
			}
			tempElement=null;
			lastElements.clear();
			
		}
		for(Element e: nounPhrases){
			String test = e.getNounPhrase()+": ";
			for(Element n: e.getNeighbour()){
				test += n.getNounPhrase()+", ";
			}
			System.out.println(test);
		}

	}
	


}
