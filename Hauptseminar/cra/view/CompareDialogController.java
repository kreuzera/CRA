package cra.view;

import java.util.Comparator;
import java.util.LinkedList;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import cra.CRAThread;
import cra.MainApp;
import cra.model.Element;
import cra.model.NounTableClass;

public class CompareDialogController {
	
    @FXML
    private TableView<NounTableClass> influenceTable1;
    @FXML
    private TableColumn<NounTableClass, String> influencePositionColumn1;
    @FXML
    private TableColumn<NounTableClass, String> influenceNounPhraseColumn1;
    @FXML
    private TableColumn<NounTableClass, String> influenceAverageColumn1;

    @FXML
    private TableView<NounTableClass> influenceTable2;
    @FXML
    private TableColumn<NounTableClass, String> influencePositionColumn2;
    @FXML
    private TableColumn<NounTableClass, String> influenceNounPhraseColumn2;
    @FXML
    private TableColumn<NounTableClass, String> influenceAverageColumn2;
    
    @FXML
    private TextArea text1;
    @FXML
    private TextArea text2;
    @FXML
    private TextField resonanceTextField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button compareButton;
    
	private Stage dialogStage;
	private MainApp mainApp;
	private ObservableList<NounTableClass> nounData1;
	private ObservableList<NounTableClass> nounData2;
	private MainViewController mainViewController;
	
	
	
	@FXML
	private void handleCompare(){
		compareButton.setDisable(true);
		mainViewController.beforeMergeCount = new AtomicInteger(0);
		nounData1.clear();
		nounData2.clear();
		CRAThread craThread = new CRAThread(null, mainViewController, null);
		craThread.setMultiThreadDijkstra(true);
		String textString1 = text1.getText();
		String textString2 = text2.getText();
		new Thread(new Runnable(){

			@Override
			public void run() {
				LinkedList<Element> influence1 = craThread.getInfluences(textString1);
				LinkedList<Element> influence2 = craThread.getInfluences(textString2);
				Comparator<Element> comparator = new Comparator<Element>(){
					@Override
					public int compare(Element arg0, Element arg1) {
						return Double.compare(arg1.getInfluence(), arg0.getInfluence());
					}
					
				};
				influence1.sort(comparator);
				influence2.sort(comparator);
				int k = 1;
				for(Element e: influence1){
					NounTableClass listItem = new NounTableClass(k, e.getNounPhrase(), Double.toString(e.getInfluence()));
					nounData1.add(listItem);
					k++;
				}
				k = 1;
				for(Element e: influence2){
					NounTableClass listItem = new NounTableClass(k, e.getNounPhrase(), Double.toString(e.getInfluence()));
					nounData2.add(listItem);
					k++;
				}
				double resonance = mainApp.getResonance(influence1, influence2, true);
				resonanceTextField.setText(Double.toString(resonance));
				
				compareButton.setDisable(false);
			}
			
		}).start();
		
	}
	
	@FXML
	private void handleClear(){
		text1.clear();
		text2.clear();
	}
	
	public void showError(){
	     Platform.runLater(new Runnable() {
	         @Override public void run() {
	             Alert alert = new Alert(AlertType.WARNING);
	             alert.initOwner(mainApp.getPrimaryStage());
	             alert.setTitle("No Texts");
	             alert.setHeaderText("No texts given");
	             alert.setContentText("Please fill both text fields with texts.");

	             alert.showAndWait();
	         }
	       });

	}
	
	public void setStatus(String text){
	     Platform.runLater(new Runnable() {
	         @Override 
	         public void run() {
        		 statusLabel.setText(text);
	         }
	       });
	}
	
	@FXML
	private void initialize() {
		nounData1 = FXCollections.observableArrayList();
		nounData2 = FXCollections.observableArrayList();
		
		influenceTable1.setItems(nounData1);
		influenceTable2.setItems(nounData2);
		
		influencePositionColumn1.setCellValueFactory(cellData -> cellData.getValue().getPosition());
		influenceNounPhraseColumn1.setCellValueFactory(cellData -> cellData.getValue().getNounPhrase());
		influenceAverageColumn1.setCellValueFactory(cellData -> cellData.getValue().getAverage());
		
		influencePositionColumn2.setCellValueFactory(cellData -> cellData.getValue().getPosition());
		influenceNounPhraseColumn2.setCellValueFactory(cellData -> cellData.getValue().getNounPhrase());
		influenceAverageColumn2.setCellValueFactory(cellData -> cellData.getValue().getAverage());
		
		compareButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if(text1.getText().length()>0&&text2.getText().length()>0){
		    		handleCompare();
		    	}else{
		    		showError();
		    	}
		    }
		});
	}
    
    public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
    
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	public MainViewController getMainViewController() {
		return mainViewController;
	}

	public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}
	
	
    
}
