package cra;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import cra.view.MainViewController;
import fileTransfer.Reader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ConcurrentLinkedQueue<String> abstracts;
        
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
    
    public Stage getPrimaryStage(){
    	return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}

	public ConcurrentLinkedQueue<String> getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(ConcurrentLinkedQueue<String> abstracts) {
		this.abstracts = abstracts;
	}
}
