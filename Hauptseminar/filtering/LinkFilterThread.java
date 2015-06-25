package filtering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import cra.model.Element;
import cra.view.MainViewController;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class LinkFilterThread extends Thread {
	private ConcurrentLinkedQueue<String> abstractList;
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases;
	private int tagger;
	private MainViewController controller;

	public LinkFilterThread(
			ConcurrentLinkedQueue<String> abstractList,
			ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases,
			int tagger, MainViewController controller) {
		this.abstractList = abstractList;
		this.nounPhrases = nounPhrases;
		this.tagger = tagger;
		this.controller = controller;
	}

	@Override
	public void run() {
		String abs = "";
		NPFilter npfilter = new NPFilter();
		// npfilter.GetTaggedWordsFromSentence("");
		ArrayList<String[]> taggedWordList = new ArrayList<String[]>();
		LinkedList<Element> lastElements = new LinkedList<Element>();
		Element tempElement = null;
		long totalTagged = 0;
		while (!abstractList.isEmpty()) {		
			abs = abstractList.poll();
			// System.out.println(abstracts.item(i).getTextContent());
			long tagStart = System.currentTimeMillis();
			switch (tagger) {
			case 0:
				taggedWordList = npfilter.GetTaggedWordsFromSentence(abs);
				break;
			case 1:
				taggedWordList = npfilter.StanfordNlpTagger(abs);
				break;
			}

			totalTagged += (System.currentTimeMillis() - tagStart);
			String lastTag = "";
			for (String[] s : taggedWordList) {
				// TODO Fix this
				if (s[0].equals("NN")
						|| s[0].equals("NNS")
						|| s[0].equals("NNP")
						|| s[0].equals("NNPS")
						|| ((s[0].equals("JJ") || s[0].equals("JJR") || s[0]
								.equals("JJS"))
								&& !(lastTag.equals("NN") || lastTag.equals("NNS")
								|| lastTag.equals("NNP") || lastTag.equals("NNPS")))) {
					String noun = s[1].toLowerCase();
//					controller.setStatus("Searching and linking nouns: "+Integer.toString(nounPhrases.size()));
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
						controller.setStatus("Searching and linking nouns: "+Integer.toString(nounPhrases.size()));
						String noun = s[1].toLowerCase();
						lastElements.add(new Element(noun));
					}
				}

					
				if (s[0].equals(".")) {
					tempElement = null;
					lastElements.clear();
				}
				lastTag = s[0];
			}
			tempElement = null;
			lastElements.clear();

		}
		// System.out.println(totalTagged);
	}

}
