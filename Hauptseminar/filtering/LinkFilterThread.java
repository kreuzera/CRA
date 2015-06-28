package filtering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import cra.model.Element;
import cra.view.MainViewController;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class LinkFilterThread extends Thread {
	private ConcurrentLinkedQueue<String> abstractList;
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases;
	private int tagger;
	private MainViewController controller;
	private static AtomicLong totalTagTime = new AtomicLong(0);

	public LinkFilterThread(
			ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases,
			MainViewController controller) {
		this.nounPhrases = nounPhrases;
		this.tagger = controller.getLibraryModel();
		this.controller = controller;
	}

	@Override
	public void run() {
		String abs = "";

		while (!abstractList.isEmpty()) {		
			abs = abstractList.poll();
			linkNounPhrases(abs);
		}
		// System.out.println(totalTagged);
	}
	
	public void linkNounPhrases(String abs){
		NPFilter npfilter = new NPFilter();
		ArrayList<String[]> taggedWordList = new ArrayList<String[]>();
		LinkedList<Element> lastElements = new LinkedList<Element>();
		Element tempElement = null;
		long tagStart = System.currentTimeMillis();
		switch (tagger) {
			case 0:
				taggedWordList = npfilter.OpenNlpTagger(abs);
				break;
			case 1:
				taggedWordList = npfilter.StanfordNlpTagger(abs);
				break;
		}
		totalTagTime.addAndGet(System.currentTimeMillis()-tagStart);
		
		String lastTag = "";
		for (String[] s : taggedWordList) {
			if (s[0].equals("NN")
					|| s[0].equals("NNS")
					|| s[0].equals("NNP")
					|| s[0].equals("NNPS")
					|| ((s[0].equals("JJ") || s[0].equals("JJR") || s[0]
							.equals("JJS"))
							&& !(lastTag.equals("NN") || lastTag.equals("NNS")
							|| lastTag.equals("NNP") || lastTag.equals("NNPS")))) {
				String noun = s[1].toLowerCase();
				lastElements.add(new Element(noun));
			} else {
				if (!lastElements.isEmpty()) {
					if (tempElement != null) {
						lastElements.getFirst().addNeighbour(tempElement);
						tempElement.addNeighbour(lastElements.getFirst());
					}
					for (Element e : lastElements) {
						for (Element n : lastElements) {
							e.addNeighbour(n);
						}
						e.getNeighbour().remove(e);
						if (nounPhrases.get(e.getNounPhrase()) != null) {
							nounPhrases
									.get(e.getNounPhrase().toLowerCase())
									.add(e);
						} else {
							ConcurrentLinkedQueue<Element> tempList = new ConcurrentLinkedQueue<Element>();
							tempList.add(e);
							nounPhrases.put(
									e.getNounPhrase().toLowerCase(),
									tempList);
						}
	
					}
					tempElement = lastElements.getLast();
					lastElements.clear();
				}
				if((s[0].equals("JJ") || s[0].equals("JJR") || s[0].equals("JJS"))
						&& (lastTag.equals("NN") || lastTag.equals("NNS")
						|| lastTag.equals("NNP") || lastTag.equals("NNPS"))){
					String noun = s[1].toLowerCase();
					lastElements.add(new Element(noun));
				}
			}
	
				
	//		if (s[0].equals(".")) {
	////			tempElement = null;
	//			lastElements.clear();
	//		}
			lastTag = s[0];
		}
		tempElement = null;
		lastElements.clear();
	
	}
	
	public static long getTotalTagTime(){
		return totalTagTime.get();
	}
	
	public static void resetTagTime(){
		totalTagTime = new AtomicLong(0);
	}
	

}
