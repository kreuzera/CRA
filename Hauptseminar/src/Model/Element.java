package Model;

import java.util.LinkedList;

public class Element {

	/**
	 * Save the String of the noun Phrase
	 */
	private String nounPhrase;
	
	
	/**
	 * List of all child-Elements
	 */
	private LinkedList<Element> neighbour;
	
	/**
	 * Number of Elements connected with current Element
	 */
	private int degree;
	
	/**
	 * Index for Unique identity
	 */
	private int index;
	
	public Element(String nounPhrase, int index){
		this.nounPhrase = nounPhrase;
		this.setIndex(index);
		neighbour = new LinkedList<Element>();
	}
	

	public String getNounPhrase() {
		return nounPhrase;
	}

	public void setNounPhrase(String nounPhrase) {
		this.nounPhrase = nounPhrase;
	}

	
	public void addNeighbour(Element e){
		neighbour.add(e);
	}

	/**
	 * Remove Element e from List of children
	 * @param e
	 */
	public void removeNeighbour(Element e){
		for(int i = 0; i < neighbour.size(); i++){
			if(neighbour.get(i)==e){
				neighbour.remove(i);
				return;
			}
		}
	}
	
	public int getDegree() {
		return degree;
	}

	public void setDegree() {
		degree = neighbour.size();
	}
	
	/**
	 * MISSING:
	 * Remove all Relationsships to e within the Graph
	 * Use Graph.searchNodeByElement or build another Method
	 * @param e
	 */
	public void merge(Element e){
		LinkedList<Element> tempList = e.getNeighbour();
		
		while(!tempList.isEmpty()){
			Element temp = tempList.poll();;
			temp.removeNeighbour(e);
			temp.addNeighbour(this);
		}
		
		neighbour.addAll(e.getNeighbour());
		setDegree();
	}
	
	public LinkedList<Element> getNeighbour(){
		return neighbour;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}
}
