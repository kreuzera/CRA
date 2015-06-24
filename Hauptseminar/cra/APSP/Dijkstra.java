package cra.APSP;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import cra.model.Element;
import cra.model.PathSet;

public class Dijkstra {
	
	public void computePaths(Element start){

		HashMap<Element, LinkedList<LinkedList<Element>>> pathList = new HashMap<Element, LinkedList<LinkedList<Element>>>();
		LinkedList<LinkedList<Element>> startList = new LinkedList<LinkedList<Element>>();
		startList.add(new LinkedList<Element>());
		pathList.put(start, startList);
		PriorityQueue<Element> candidates = new PriorityQueue<Element>(new Comparator<Element>(){

			@Override
			public int compare(Element arg0, Element arg1) {
				if(pathList.get(arg0).peek() == null && pathList.get(arg1).peek()!=null)
					return 1;
				if(pathList.get(arg0).peek() != null && pathList.get(arg1).peek()==null)
					return -1;
				if(pathList.get(arg0).peek() == null && pathList.get(arg1).peek() == null)
					return 0;
							
				if(pathList.get(arg0).peek().size()<pathList.get(arg1).peek().size())
					return -1;
				if(pathList.get(arg0).peek().size()>pathList.get(arg1).peek().size())
					return 1;
				return 0;
			}
			
		});
		
		candidates.add(start);
		int counter = 0;
		while(!candidates.isEmpty()){
			Element candidate = candidates.poll();
			counter++;
			
			for(Element n: candidate.getNeighbour()){
				LinkedList<LinkedList<Element>> neighboursPathList = pathList.get(n);
				if(neighboursPathList==null){
					neighboursPathList = new LinkedList<LinkedList<Element>>();
					pathList.put(n, neighboursPathList);
				}
				if(neighboursPathList.peek() == null || (pathList.get(candidate).peek().size()+1)<=neighboursPathList.peek().size()){
					if(neighboursPathList.peek()==null || (pathList.get(candidate).peek().size()+1)<neighboursPathList.peek().size()){
						neighboursPathList.clear();
						candidates.add(n);
					}						
					for(LinkedList<Element> cPaths: pathList.get(candidate)){
						LinkedList<Element> newList = copyPathList(cPaths);
						newList.add(n);
						neighboursPathList.add(newList);
						pathList.put(n, neighboursPathList);						
					}
				}
				
			}
		}
		for(LinkedList<LinkedList<Element>> c: pathList.values()){
			for(LinkedList<Element> d: c){
				PathSet pathset = new PathSet(d.peekFirst(), d.peekLast(), d);
				start.shortestPaths.add(pathset);
			}
		}
		
		
	}
	
	private LinkedList<Element> copyPathList(LinkedList<Element> list){
		LinkedList<Element> copyList = new LinkedList<Element>();
		for(Element e: list){
			copyList.add(e);
		}
		return copyList;
	}

}
