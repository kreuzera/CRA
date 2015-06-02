package craMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


import java.util.concurrent.ConcurrentLinkedQueue;

import model.Element;

import org.w3c.dom.NodeList;

import fileTransfer.FileReader;
import filtering.NPFilter;

public class CRAMain {

	public static void main(String[] args) {
		FileReader fileReader = new FileReader();
		NodeList abstracts = fileReader.getAbstracts("testFile/ebscohost export.xml");
		
		NPFilter npfilter = new NPFilter();
		npfilter.GetTaggedWordsFromSentence("");
		
		ArrayList<String[]> taggedWordList = new ArrayList<String []>();
		HashMap<String, LinkedList<Element>> nounPhrases = new HashMap<String, LinkedList<Element>>();
		LinkedList<Element> lastElements = new LinkedList<Element>();
		Element tempElement = null;
		System.out.print("Tagging and linking ...");
		long start = System.currentTimeMillis();
		for (int i = 0; i<abstracts.getLength(); i++){
//			System.out.println(abstracts.item(i).getTextContent());
			taggedWordList = npfilter.GetTaggedWordsFromSentence(abstracts.item(i).getTextContent());
			
			for(String[] s : taggedWordList){
				if(s[0].equals("NN")||s[0].equals("NNS")||s[0].equals("NNP")||s[0].equals("NNPS")||s[0].equals("JJ")||s[0].equals("JJR")||s[0].equals("JJS")){					
					lastElements.add(new Element(s[1].toLowerCase()));
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
							if(nounPhrases.get(e.getNounPhrase())!=null){
								nounPhrases.get(e.getNounPhrase().toLowerCase()).add(e);
							}else{
								LinkedList<Element>tempList = new LinkedList<Element>();
								tempList.add(e);
								nounPhrases.put(e.getNounPhrase().toLowerCase(), tempList);
							}
								
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
		System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms");
		// Merge
		System.out.print("Merging ...");
		start = System.currentTimeMillis();
		int i = 0;
		for(LinkedList<Element> eList: nounPhrases.values()){
			Element first = eList.getFirst();
			for(Element e: eList){
				i++;
				if(e!=first){
					for(Element n: e.getNeighbour()){
						if(!first.getNeighbour().contains(n))
							first.addNeighbour(n);
					}					
				}					
			}
		}
		System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms. "+i+" nouns merged into "+nounPhrases.size()+" nouns. Printing in 5 Seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Print
		for(LinkedList<Element> eList: nounPhrases.values()){
			String testung = eList.getFirst().getNounPhrase()+": ";
			for(Element n: eList.getFirst().getNeighbour()){
				testung += n.getNounPhrase()+", ";
			}
			System.out.println(testung);
		}


	}
	


}
