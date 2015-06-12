package craMain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;


import java.util.concurrent.ConcurrentLinkedQueue;

import model.Element;

import org.w3c.dom.NodeList;

import fileTransfer.FileReader;
import filtering.NPFilter;

public class CRAMain {

	public static void main(String[] args) {
		
//		}
		for(int i=0; i<2; i++){
			if(i==0)
				System.out.println("using OpenNLP Tagger Model");
			else
				System.out.println("using stanfordNLP Tagger Model");
			CRA(i);
		}

		
	}
	
	static void CRA(int tagger){
		long totalTime = System.currentTimeMillis();
		FileReader fileReader = new FileReader();
//		NodeList abstracts = fileReader.getAbstracts("testFile/ebscohost export.xml");
		NodeList abstracts = fileReader.getAbstracts("testFile/8dc0a169-d1be-468a-b7a0-1a90a4ba3a50.xml");
		
		NPFilter npfilter = new NPFilter();
		//npfilter.GetTaggedWordsFromSentence("");
		
		//npfilter.StanfordNlpTagger("Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper.");
		
		ArrayList<String[]> taggedWordList = new ArrayList<String []>();
		HashMap<String, LinkedList<Element>> nounPhrases = new HashMap<String, LinkedList<Element>>();
		LinkedList<Element> lastElements = new LinkedList<Element>();
		Element tempElement = null;
		System.out.println("Tagging and linking ...");
		long start = System.currentTimeMillis();
		for (int i = 0; i<abstracts.getLength(); i++){
//			System.out.println(abstracts.item(i).getTextContent());
			if(tagger==0)
				taggedWordList = npfilter.GetTaggedWordsFromSentence(abstracts.item(i).getTextContent());
			else
				taggedWordList = npfilter.StanfordNlpTagger(abstracts.item(i).getTextContent());			
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
		System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms. "+i+" nouns merged into "+nounPhrases.size()+" nouns.");
		System.out.println("Total time: "+(System.currentTimeMillis()-totalTime)+"ms. Printing in 5 Seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Print
		int k = 0;
		LinkedList<Element> test = new LinkedList<Element>();
		for(LinkedList<Element> eList: nounPhrases.values()){
			test.add(eList.getFirst());
		}
		test.sort(new Comparator<Element>(){
			@Override
			public int compare(Element o1, Element o2) {
				if(o1.getNeighbour().size()>o2.getNeighbour().size())
					return -1;
				if(o1.getNeighbour().size()<o2.getNeighbour().size())
					return 1;
				return 0;
			}
			
		});
		
		for(Element e: test){
			String testung = e.getNounPhrase()+": "+e.getNeighbour().size();
			System.out.println(testung);
			if(k>30)
				break;
			k++;
		}
//		for(LinkedList<Element> eList: nounPhrases.values()){
//			String testung = eList.getFirst().getNounPhrase()+": ";
//			for(Element n: eList.getFirst().getNeighbour()){
//				testung += n.getNounPhrase()+", ";
//			}
//			System.out.println(testung);
	}


}
