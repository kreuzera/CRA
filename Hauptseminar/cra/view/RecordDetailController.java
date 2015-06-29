package cra.view;

import cra.model.ResonanceTableClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RecordDetailController {
	
	private Stage dialogStage;
	
	@FXML
	private Label titel;
	
	@FXML
	private Label Authors;
	
	@FXML
	private TextArea abstracts;
	
    @FXML
    private void initialize() {
    	
    }

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setRecord(ResonanceTableClass rec){
		
	}

}
