package cra;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

import cra.model.Element;
import cra.model.NounTableClass;
import cra.model.Record;
import cra.model.ResonanceTableClass;
import cra.view.MainViewController;
import cra.view.RecordDetailController;
import fileTransfer.Reader;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ConcurrentLinkedQueue<Record> abstracts;
    private File file;
    private ObservableList<NounTableClass> nounData = FXCollections.observableArrayList();
    private ObservableList<ResonanceTableClass> resonanceData = FXCollections.observableArrayList();
        
	@Override
	public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CRA");
        
        // Set the application icon.
//        this.primaryStage.getIcons().add(new Image("file:resources/images/Address_Book.png"));

        initRootLayout();

	}
	
	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            // Give the controller access to the main app.
            MainViewController controller = loader.getController();
            controller.setMainApp(this);
            
           
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
//        // Try to load last opened person file.
//        File file = getPersonFilePath();
//        if (file != null) {
//            loadPersonDataFromFile(file);
//        }
    }
    
    public void loadXml(File xmlFile){
		Reader fileReader = new Reader();
    	setAbstracts(fileReader.getAbstracts(xmlFile));
    }
    
    private static void openWebpages(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpages(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    public void showRecordDetail(ResonanceTableClass res){
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/RecordDetail.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Person");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        RecordDetailController controller = loader.getController();
        controller.setDialogStage(dialogStage);
//        controller.setPerson(person);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
    }
    
    public Stage getPrimaryStage(){
    	return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}

	public ConcurrentLinkedQueue<Record> getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(ConcurrentLinkedQueue<Record> abstracts) {
		this.abstracts = abstracts;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public ObservableList<NounTableClass> getNounData() {
		return nounData;
	}

	public void setNounData(ObservableList<NounTableClass> nounData) {
		this.nounData = nounData;
	}

	public ObservableList<ResonanceTableClass> getResonanceData() {
		return resonanceData;
	}

	public void setResonanceData(ObservableList<ResonanceTableClass> resonanceData) {
		this.resonanceData = resonanceData;
	}


}
