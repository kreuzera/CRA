package cra.view;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import cra.MainApp;
import cra.model.Element;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import filtering.LinkFilterThread;
import filtering.NPFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.FileChooser;

public class MainViewController {
	
	@FXML
	private Spinner numPrint;
	
	@FXML
	private CheckBox printNeighbors;
	
	@FXML
	private Label filePath;
	
	@FXML
	private ComboBox<String> library;
	
	@FXML
	private ComboBox<String> measure;
	
	private MainApp mainApp;
	
	
	
	public MainViewController(){
		
	}
	
	@FXML
	private void handleBrowse(){
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadXml(file);
            filePath.setText(file.getAbsolutePath());
        }
	}
	
	@FXML
	private void handleAnalyse(){
		long totalTime = System.currentTimeMillis();
		NPFilter npfilter = new NPFilter();
		switch(library.getSelectionModel().getSelectedIndex()){
			case 0:
				npfilter.GetTaggedWordsFromSentence("");
				break;
			case 1:
				NPFilter.stanfordTagger = new MaxentTagger("english-left3words-distsim.tagger");
				break;
			default:
				npfilter.GetTaggedWordsFromSentence("");
				break;
		}
		
//		npfilter.GetTaggedWordsFromSentence("");

		ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>>();
		System.out.print("Tagging and linking ... ");
		long start = System.currentTimeMillis();
		ConcurrentLinkedQueue<String> abstracts = new ConcurrentLinkedQueue<String>();
		for(String s: mainApp.getAbstracts()){
			abstracts.add(s);
		}
		LinkedList<LinkFilterThread> threadList = new LinkedList<LinkFilterThread>();
		for(int i = 0; i<Runtime.getRuntime().availableProcessors(); i++){
			LinkFilterThread thread = new LinkFilterThread(abstracts, nounPhrases, library.getSelectionModel().getSelectedIndex());
			thread.setName(Integer.toString(i));
			threadList.add(thread);
			thread.start();
		}
		System.out.print("Thread count: "+threadList.size()+" ... ");
		for(LinkFilterThread t: threadList){
			try {
				t.join();
//				System.out.println(t.getName()+" is alive? "+t.isAlive());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms");
		// Merge
		System.out.print("Merging ...");
		start = System.currentTimeMillis();
		int i = 0;
		for(ConcurrentLinkedQueue<Element> eList: nounPhrases.values()){
			Element first = eList.peek();
			if(eList!=null)
			for(Element e: eList){
				i++;
				if(e!=first){
					for(Element n: e.getNeighbour()){
						if(!first.getNeighbour().contains(n))
							first.addNeighbour(n);
					}					
				}					
			}
		}
		System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms. "+i+" nouns merged into "+nounPhrases.size()+" nouns. ");
		System.out.println("Total time: "+(System.currentTimeMillis()-totalTime)+"ms. Printing in 5 Seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Print
		int k = 0;
		LinkedList<Element> test = new LinkedList<Element>();
		for(ConcurrentLinkedQueue<Element> eList: nounPhrases.values()){
			test.add(eList.peek());
		}
		test.sort(new Comparator<Element>(){
			@Override
			public int compare(Element o1, Element o2) {
				if(o1.getNeighbour().size()>o2.getNeighbour().size())
					return -1;
				if(o1.getNeighbour().size()<o2.getNeighbour().size())
					return 1;
				return 0;
			}
			
		});
		
		for(Element e: test){
			String testung = e.getNounPhrase()+": "+e.getNeighbour().size();
			System.out.println(testung);
			if(k>30)
				break;
			k++;
		}
	}
	
	@FXML
	private void initialize() {
		ObservableList<String> libraryContent = FXCollections.observableArrayList(
				"Open NLP",
				"Stanford NLP"
				);
		library.setItems(libraryContent);
		library.getSelectionModel().selectFirst();
		ObservableList<String> measureContent = FXCollections.observableArrayList(
				"Betweeness centrality",
				"Degree centrality",
				"Closness centrality"
				);
		measure.setItems(measureContent);
		measure.getSelectionModel().selectFirst();
		//Load Tokenizers
		new Thread(new Runnable() {
            @Override
            public void run() {
        		NPFilter npFilter = new NPFilter();
        		npFilter.GetTaggedWordsFromSentence("");
        		NPFilter.stanfordTagger = new MaxentTagger("english-left3words-distsim.tagger");
        }}).start();

	}

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
