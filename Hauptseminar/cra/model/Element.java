package cra.model;

import java.util.LinkedList;

public class Element implements Comparable{

	//Datastructure for CRA
	private String nounPhrase;
	private LinkedList<Element> neighbour;
	private int degree;
	private int index;
	private float influence = 0f;
	private int id = 0;
	
	//Datastructure for APSP
	private double minDistance = Double.POSITIVE_INFINITY;
	private Element previous;
	public LinkedList<PathSet> shortestPaths = new LinkedList<PathSet>();
	
	
	public Element(String nounPhrase){
		this.nounPhrase = nounPhrase;
		this.neighbour = new LinkedList<Element>();
	}
	
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
	
	public Element getPrevious(){
		return previous;
	}
	
	public void setPrevious(Element e){
		previous = e;
	}
	
	public double getMinDistance() {
		return minDistance;
	}
	
	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}
	
	@Override
	public boolean equals(Object o){
		if(o!=null && o instanceof Element && ((Element)o).getNounPhrase().equals(this.nounPhrase))
			return true;
		return false;
	}

	
	public int compareTo(Element other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

	@Override
	public int compareTo(Object o) {
		if(o instanceof Element)
			return compareTo((Element)o);
		return 0;
	}

	public float getInfluence() {
		return influence;
	}

	public void setInfluence(float influence) {
		this.influence = influence;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}