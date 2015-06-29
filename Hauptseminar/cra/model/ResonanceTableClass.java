package cra.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResonanceTableClass {
	private final StringProperty position;
	private final StringProperty textName;
	private final StringProperty resonance;
	
	public ResonanceTableClass(int position, String textName, String resonance){
		this.position = new SimpleStringProperty(Integer.toString(position));
		this.resonance = new SimpleStringProperty(resonance);
		this.textName = new SimpleStringProperty(textName);
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
	
}
