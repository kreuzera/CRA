package cra.model;

import java.util.LinkedList;

public class PathSet {

	private Element source;
	private Element target;
	private LinkedList<Element> path = new LinkedList<Element>();
	
	public PathSet(Element source, Element target, LinkedList<Element> path){
		this.source = source;
		this.target = target;
		this.path = path;
	}
	
	public Element getSource() {
		return source;
	}
	public void setSource(Element source) {
		this.source = source;
	}
	public Element getTarget() {
		return target;
	}
	public void setTarget(Element target) {
		this.target = target;
	}
	public LinkedList<Element> getPath() {
		return path;
	}
	public void setPath(LinkedList<Element> path) {
		this.path = path;
	}
}
