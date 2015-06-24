package cra.APSP;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cra.model.Element;
import cra.model.PathSet;
import cra.view.MainViewController;

public class DijkstraRec extends Thread{
	private HashMap<Element, LinkedList<Element>> parentList;
	private ConcurrentLinkedQueue<Element> listOfNodes;
	private MainViewController controller;
	
	public DijkstraRec(ConcurrentLinkedQueue<Element> listOfNodes, MainViewController controller){
		this.listOfNodes = listOfNodes;
		this.controller = controller;
	}
	
	@Override
	public void run(){
		while(!listOfNodes.isEmpty()){
			Element e = listOfNodes.poll();
			computePaths(e);
			HashMap<Element, Integer> overList = new HashMap<Element, Integer>();
			int totalPaths = 0;
			Element last = null;
			if(!e.shortestPaths.isEmpty())
				 last = e.shortestPaths.getFirst().getTarget();
			for(PathSet path: e.shortestPaths){						
				totalPaths++;
				for(Element pElement: path.getPath()){
					if(overList.get(pElement)==null)
						overList.put(pElement, 0);
					Integer tempInt = overList.get(pElement);
					tempInt++;
					overList.put(pElement, tempInt);
				}
				
				if(path.getTarget()!=last){
					for(Element in: overList.keySet()){
						float influence = in.getInfluence()+(overList.get(in)/totalPaths);
						in.setInfluence(influence);
					}
					totalPaths = 0;
					overList.clear();
				}
				last = path.getTarget();
			}
			e.shortestPaths.clear();
			controller.counter++;
		}
	}
	
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
		distance.clear();
		for(Element e: visited){
			if(e!=start && start.getId()<e.getId())
				fillPaths(e, new LinkedList<Element>());
		}		
		
	}
	
	
	private void fillPaths(Element element, LinkedList<Element> currentPath){
		currentPath.addFirst(element);
		
		if(parentList.get(element).size() == 0){
			PathSet path = new PathSet(element, currentPath.getLast(), copyList(currentPath));
			currentPath = null;
			element.shortestPaths.add(path);
		}else{
			if(parentList.get(element).size()==1){
				fillPaths(parentList.get(element).peek(), currentPath);
			}
			else{
				for(Element parent: parentList.get(element)){
					fillPaths(parent, copyList(currentPath));
				}
			}
		}
		currentPath=null;
		
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
