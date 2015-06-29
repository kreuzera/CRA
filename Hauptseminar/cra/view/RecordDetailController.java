package cra.view;

import cra.model.ResonanceTableClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RecordDetailController {
	
	private Stage dialogStage;
	
	@FXML
	private TextField titel;
	
	@FXML
	private TextField authors;
	
	@FXML
	private TextArea abstracts;
	
    @FXML
    private void initialize() {
    	
    }

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setRecord(ResonanceTableClass rec){
		titel.setText(rec.getTextName().get());
		authors.setText(rec.getAuthors().get());
		abstracts.setText(rec.getAbstracts().get());
	}

}
