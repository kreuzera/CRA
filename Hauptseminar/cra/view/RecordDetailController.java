package cra.view;

import java.net.MalformedURLException;
import java.net.URL;

import cra.MainApp;
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
	
	private MainApp mainApp;
	
	private ResonanceTableClass rec;
	
    @FXML
    private void initialize() {
    	
    }
    
    @FXML
    private void handleOpenBrowser(){
    	URL url;
		try {
			url = new URL("http://search.ebscohost.com/login.aspx?direct=true&authtype=ip,uid&db="+rec.getDbID().get()+"&an="+rec.getUrlID().get());
			mainApp.openWebpage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setRecord(ResonanceTableClass rec){
		this.rec = rec;
		titel.setText(rec.getTextName().get());
		authors.setText(rec.getAuthors().get());
		abstracts.setText(rec.getAbstracts().get());
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	

}
