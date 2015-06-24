package cra.APSP;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import cra.model.Element;
import cra.model.PathSet;

public class DijkstraRec {
	private HashMap<Element, LinkedList<Element>> parentList;
	
	public void computePaths(Element start){
		parentList = new HashMap<Element, LinkedList<Element>>();
		HashMap<Element, Integer> distance = new HashMap<Element, Integer>();
		HashSet<Element> visited = new HashSet<Element>();
		PriorityQueue<Element> cList = new PriorityQueue<Element>(new Comparator<Element>(){

			@Override
			public int compare(Element arg0, Element arg1) {
				if(distance.get(arg0) == null && distance.get(arg1)!=null)
					return 1;
				if(distance.get(arg0) != null && distance.get(arg1)==null)
					return -1;
				if(distance.get(arg0) == null && distance.get(arg1)==null)
					return 0;
				if(distance.get(arg0)>distance.get(arg1))
					return 1;
				if(distance.get(arg0)<distance.get(arg1))
					return -1;
				return 0;
			}
			
		});
		
		distance.put(start, 0);
		cList.add(start);
		
		while(!cList.isEmpty()){
			Element candidate = cList.poll();
			visited.add(candidate);
			for(Element n: candidate.getNeighbour()){
				if(distance.get(n)==null)
					distance.put(n, Integer.MAX_VALUE);
				if(parentList.get(n)==null)
					parentList.put(n, new LinkedList<Element>());
				int nDistance = distance.get(n);
				
				if((distance.get(candidate)+1)<nDistance){
					parentList.get(n).clear();
					parentList.get(n).add(candidate);
					distance.put(n, distance.get(candidate)+1);
					cList.add(n);
				}
				if((distance.get(candidate)+1) == nDistance){
					if(!parentList.get(n).contains(candidate))
						parentList.get(n).add(candidate);
				}
			}
		}
		for(Element e: visited){
			if(e!=start)
				fillPaths(e, new LinkedList<Element>());
		}		
		
	}
	
	private void fillPaths(Element element, LinkedList<Element> currentPath){
		currentPath.addFirst(element);
		if(parentList.get(element).size() == 0){
			PathSet path = new PathSet(element, currentPath.getLast(), currentPath);
			element.shortestPaths.add(path);
		}else{
			if(parentList.get(element).size()==1){
				fillPaths(parentList.get(element).peek(), currentPath);
			}
			else{
				for(Element parent: parentList.get(element)){
					currentPath = copyList(currentPath);
					fillPaths(parent, currentPath);
				}
			}
		}
		
	}
	public static long copyTime = 0;
	private LinkedList<Element>copyList (LinkedList<Element> eList){
		long start = System.currentTimeMillis();
		LinkedList<Element> copy = new LinkedList<Element>();
		for(Element e: eList){
			copy.add(e);
		}
		copyTime += (System.currentTimeMillis()-start);
		return copy;
	}

}
