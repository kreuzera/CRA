package cra;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import cra.APSP.Algorithmus;
import cra.APSP.Dijkstra;
import cra.model.Element;
import cra.model.PathSet;
import cra.model.Record;
import cra.view.MainViewController;
import filtering.LinkFilterThread;

public class CRAThread extends Thread{
	private MainViewController controller;
	private ConcurrentLinkedQueue<Record> recordList;
	private ConcurrentLinkedQueue<Record> targetList;
	
	public CRAThread(ConcurrentLinkedQueue<Record> recordList, MainViewController controller, ConcurrentLinkedQueue<Record> targetList){
		this.targetList = targetList;
		this.recordList = recordList;
		this.controller = controller;
	}
	
	@Override
	public void run(){
		String abs = "";
		while(!recordList.isEmpty()){
			Record record = recordList.poll();
			abs = record.getAbstractText();
			
			//TODO REMOVE THIS BEFORE SUBMISSION
	//		NPFilter test = new NPFilter();
	//		test.test("Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper,");
	
			ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>>();
	
			LinkFilterThread linkFilter = new LinkFilterThread(nounPhrases, controller);
			linkFilter.linkNounPhrases(abs);
			
			// Merge
			LinkedList<Element> mergeList = new LinkedList<Element>();
			int i = 0;
			int id = 0;
			for(ConcurrentLinkedQueue<Element> eList: nounPhrases.values()){
				Element mergeTarget = new Element(eList.peek().getNounPhrase());
				mergeTarget.setId(id);
				id++;
				if(eList!=null)
				for(Element e: eList){
					i++;
					for(Element n: e.getNeighbour()){
						if(!mergeTarget.getNeighbour().contains(n))
							mergeTarget.addNeighbour(n);
						n.getNeighbour().remove(e);
						n.addNeighbour(mergeTarget);
					}					
				}
				mergeList.add(mergeTarget);
			}
			controller.beforeMergeCount.addAndGet(i);
			
			switch(controller.getMeasureModel()){
				case 0:
					ConcurrentLinkedQueue<Element> eList = new ConcurrentLinkedQueue<Element>();
					Dijkstra dijkstra = new Dijkstra(eList, controller);
					for(Element e: mergeList){
	//					eList.add(e);
						dijkstra.computePaths(e);
					}
					
					

					for(Element e: mergeList){
						if(((mergeList.size()-1)*(mergeList.size()-2)/2)>0){
							float influence = e.getInfluence()/((float)(mergeList.size()-1)*(mergeList.size()-2)/2);
							e.setInfluence(influence);
						}
					}
					
					break;
				case 1:
					mergeList.sort(new Comparator<Element>(){
						@Override
						public int compare(Element o1, Element o2) {
							if(o1.getNeighbour().size()>o2.getNeighbour().size())
								return -1;
							if(o1.getNeighbour().size()<o2.getNeighbour().size())
								return 1;
							return 0;
						}
						
					});
	
					break;
				default:
					break;
			}
			record.setProcessedNP(mergeList);
			targetList.add(record);
			controller.wordCount.addAndGet(mergeList.size());
			controller.counter.incrementAndGet();
		}
	}

}
