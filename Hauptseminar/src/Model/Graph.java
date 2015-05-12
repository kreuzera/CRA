package Model;

import java.util.LinkedList;

public class Graph {

	private Element head;
	
	
	/***
	 * Search the Element with given Noun-Phrase
	 * Compare current element.nounPhrase with searched nounPhrase
	 * TRUE: return current
	 * FALSE: add all Children of current in toDo(nodes for further searches) and add current to visited(avoid cycle)
	 * @param nounPhrase nounPhrase you are looking for
	 * @return
	 */
	public Element searchNodeByString(String nounPhrase){
		
		LinkedList<Element>toDo = new LinkedList<Element>();
		LinkedList<Element>visited = new LinkedList<Element>();
		
		if(head.getNounPhrase().equals(nounPhrase))
			return head;
		
		toDo.addAll(head.getNeighbour());
		System.out.println(toDo.size());
		
		visited.add(head);
		
		while(!toDo.isEmpty()){
			Element temp = toDo.poll();
			
			if(!visited.contains(temp)){
				if(temp.getNounPhrase().equals(nounPhrase))
					return temp;
				
				toDo.addAll(temp.getNeighbour());
				visited.add(temp);
			}
		}
		
		return null;
	}
	
	/***
	 * same like searchNodeByString, but look for Element not for nounPhrase
	 * @param element
	 * @return
	 */
	public Element searchNodeByElement(Element element){
		
		LinkedList<Element>toDo = new LinkedList<Element>();
		LinkedList<Element>visited = new LinkedList<Element>();
		
		if(head==element)
			return head;
		
		toDo.addAll(head.getNeighbour());
		System.out.println(toDo.size());
		
		visited.add(head);
		
		while(!toDo.isEmpty()){
			Element temp = toDo.poll();
			
			if(!visited.contains(temp)){
				if(temp==element)
					return temp;
				
				toDo.addAll(temp.getNeighbour());
				visited.add(temp);
			}
		}
		
		return null;
	}
	
	public void setHead(Element e){
		head = e;
	}
	
	public Element getHead(){
		return head;
	}
	
	public void linkElements(Element e1, Element e2){
		e1.addNeighbour(e2);
		e2.addNeighbour(e1);
	}
}
