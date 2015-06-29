package cra.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NounTableClass {
	private final StringProperty position;
	private final StringProperty nounPhrase;
	private final StringProperty average;
	
	public NounTableClass(int position, String nounPhrase, String average){
		this.position = new SimpleStringProperty(Integer.toString(position));
		this.nounPhrase = new SimpleStringProperty(nounPhrase);
		this.average = new SimpleStringProperty(average);
	}

	public StringProperty getPosition() {
		return position;
	}

	public StringProperty getNounPhrase() {
		return nounPhrase;
	}

	public StringProperty getAverage() {
		return average;
	}

}
