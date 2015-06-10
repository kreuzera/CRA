package craMain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import model.Element;

import org.w3c.dom.NodeList;

import fileTransfer.FileReader;
import filtering.LinkFilterThread;
import filtering.NPFilter;

public class CRAMain {

	public static void main(String[] args) {
		long totalTime = System.currentTimeMillis();
		FileReader fileReader = new FileReader();
//		NodeList abstracts = fileReader.getAbstracts("testFile/ebscohost export.xml");
		ConcurrentLinkedQueue<String> abstracts = fileReader.getAbstracts("C:/Users/Kai/Downloads/8dc0a169-d1be-468a-b7a0-1a90a4ba3a50/8dc0a169-d1be-468a-b7a0-1a90a4ba3a50.xml");
		
		
		NPFilter npfilter = new NPFilter();
		npfilter.GetTaggedWordsFromSentence("");

		ConcurrentHashMap<String, LinkedList<Element>> nounPhrases = new ConcurrentHashMap<String, LinkedList<Element>>();
		System.out.print("Tagging and linking ... ");
		long start = System.currentTimeMillis();
		LinkedList<LinkFilterThread> threadList = new LinkedList<LinkFilterThread>();
		for(int i = 0; i<Runtime.getRuntime().availableProcessors(); i++){
			LinkFilterThread thread = new LinkFilterThread(abstracts, nounPhrases);
			threadList.add(thread);
			thread.start();
		}
		System.out.print("Thread count: "+threadList.size()+" ... ");
		for(LinkFilterThread t: threadList){
			try {
				t.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
		System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms. "+i+" nouns merged into "+nounPhrases.size()+" nouns. ");
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
//			String testung = eList.getFirst().getNounPhrase()+": "+eList.getFirst().getNeighbour().size();
//			for(Element n: eList.getFirst().getNeighbour()){
//				testung += n.getNounPhrase()+", ";
//			}
//			System.out.println(testung);
//			if(k>30)
//				break;
//			k++;
//		}


	}
	


}
