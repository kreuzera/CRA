package cra.model;

import java.util.LinkedList;

public class Record {
	private String title ="";
	private String url = "";
	private String journal = "";
	private String abstractText = "";
	private LinkedList<String> authors = new LinkedList<String>();
	private LinkedList<Element> processedNP;
	private float resonance = 0f;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	public LinkedList<String> getAuthors() {
		return authors;
	}
	public void setAuthors(LinkedList<String> authors) {
		this.authors = authors;
	}
	
	public void addAuthor(String author){
		this.authors.add(author);
	}
	public LinkedList<Element> getProcessedNP() {
		return processedNP;
	}
	public void setProcessedNP(LinkedList<Element> processedNP) {
		this.processedNP = processedNP;
	}
	public float getResonance() {
		return resonance;
	}
	public void setResonance(float resonance) {
		this.resonance = resonance;
	}
	

}
