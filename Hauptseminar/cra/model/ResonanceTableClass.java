package cra.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResonanceTableClass {
	private final StringProperty position;
	private final StringProperty textName;
	private final StringProperty resonance;
	private final StringProperty urlID;
	private final StringProperty authors;
	private final StringProperty abstracts;
	
	public ResonanceTableClass(int position, String textName, String resonance, String urlID, String authors, String abstracts){
		this.position = new SimpleStringProperty(Integer.toString(position));
		this.resonance = new SimpleStringProperty(resonance);
		this.textName = new SimpleStringProperty(textName);
		this.urlID = new SimpleStringProperty(urlID);
		this.authors = new SimpleStringProperty(authors);
		this.abstracts = new SimpleStringProperty(abstracts);
	}

	public StringProperty getPosition() {
		return position;
	}

	public StringProperty getTextName() {
		return textName;
	}

	public StringProperty getResonance() {
		return resonance;
	}

	public StringProperty getUrlID() {
		return urlID;
	}

	public StringProperty getAuthors() {
		return authors;
	}

	public StringProperty getAbstracts() {
		return abstracts;
	}
	
}
