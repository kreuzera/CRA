package filtering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import model.Element;

public class LinkFilterThread extends Thread{
	ConcurrentLinkedQueue<String> abstractList;
	ConcurrentHashMap<String, LinkedList<Element>> nounPhrases;
	
	public LinkFilterThread(ConcurrentLinkedQueue<String> abstractList, ConcurrentHashMap<String, LinkedList<Element>> nounPhrases) {
		this.abstractList=abstractList;
		this.nounPhrases=nounPhrases;
	}

	@Override
	public void run(){
		String abs="";
		NPFilter npfilter = new NPFilter();
		npfilter.GetTaggedWordsFromSentence("");
		ArrayList<String[]> taggedWordList = new ArrayList<String []>();
		LinkedList<Element> lastElements = new LinkedList<Element>();
		Element tempElement = null;
		while(!abstractList.isEmpty()){
			abs = abstractList.poll();
//			System.out.println(abstracts.item(i).getTextContent());
			taggedWordList = npfilter.GetTaggedWordsFromSentence(abs);
			
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
	}

}
