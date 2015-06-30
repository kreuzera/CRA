package cra.view;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cra.CRAThread;
import cra.MainApp;
import cra.APSP.Algorithmus;
import cra.APSP.Dijkstra;
import cra.model.NounTableClass;
import cra.model.Record;
import cra.model.Element;
import cra.model.PathSet;
import cra.model.ResonanceTableClass;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import fileTransfer.Reader;
import filtering.LinkFilterThread;
import filtering.NPFilter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class MainViewController {
	
	@FXML
	private CheckBox printNeighbors;
	
	@FXML
	private Button analyseButton;
	
	@FXML
	private Label filePath;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private ComboBox<String> library;
	
	@FXML
	private ComboBox<String> measure;
	
	@FXML
	private Spinner<Integer> printQuantity;
	
	@FXML
	private CheckBox weightedResonance;
	
	
    @FXML
    private TableView<NounTableClass> influenceTable;
    @FXML
    private TableColumn<NounTableClass, String> influencePositionColumn;
    @FXML
    private TableColumn<NounTableClass, String> influenceNounPhraseColumn;
    @FXML
    private TableColumn<NounTableClass, String> influenceAverageColumn;
    
    @FXML
    private TableView<ResonanceTableClass> resonanceTable;
    @FXML
    private TableColumn<ResonanceTableClass, String> resonancePositionColumn;
    @FXML
    private TableColumn<ResonanceTableClass, String> resonancenTextNameColumn;
    @FXML
    private TableColumn<ResonanceTableClass, String> resonancenResonanceColumn;
	
	private MainApp mainApp;
	
	public AtomicInteger counter;
	public AtomicInteger wordCount;
	public AtomicInteger beforeMergeCount;
	private static boolean algoFinished = false;
	
	
	
	public MainViewController(){
		
	}
	
	@FXML
	private void handleBrowse(){
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        
        //TODO TO IMPLEMENT OR NOT TO IMPLEMENT
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter(
                "TXT files (*.txt)", "*.txt");        
        fileChooser.getExtensionFilters().add(txtFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
        	mainApp.setFile(file);
            mainApp.loadXml(file);
            filePath.setText(file.getAbsolutePath());
        }

	}
	
	@FXML
	private void handleAnalyse(){
		MainViewController controller = this;
		LinkFilterThread.resetTagTime();
		mainApp.getNounData().clear();
		mainApp.getResonanceData().clear();
		Thread m = new Thread(new Runnable(){

			@Override
			public void run() {
				analyseButton.setDisable(true);
				
				counter = new AtomicInteger(0);
				wordCount = new AtomicInteger(0);
				beforeMergeCount = new AtomicInteger(0);
				long algoDuration = System.currentTimeMillis();
				String print = "";

				//TODO REMOVE THIS BEFORE SUBMISSION
//				NPFilter test = new NPFilter();
//				test.test("Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper,");
				
				ConcurrentLinkedQueue<Record> recordList = new ConcurrentLinkedQueue<Record>();
				recordList = mainApp.getAbstracts();
				if(recordList.size()==0){
					mainApp.loadXml(mainApp.getFile());
					recordList = mainApp.getAbstracts();
				}
				int numberOfAbstract = recordList.size();
				algoFinished = false;
				new Thread(new Runnable() {
		            @Override
		            public void run() {
		        		try {
		        			while(!algoFinished){
		        				String status = "Analysing abstracts: "+Math.round(((float)counter.get()/numberOfAbstract)*100)+"% in "+getDurationBreakdown(System.currentTimeMillis()-algoDuration);
		        				System.out.println(status);
		        				setStatus(status);
		        				Thread.sleep(1000);        				
		        			}		
		        		} catch (InterruptedException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
		        }}).start();
				LinkedList<CRAThread> threadList = new LinkedList<CRAThread>();
				ConcurrentLinkedQueue<Record> targetList = new ConcurrentLinkedQueue<Record>();
				for(int i = 0; i< Runtime.getRuntime().availableProcessors(); i++){
					CRAThread thread = new CRAThread(recordList, controller, targetList);
					threadList.add(thread);
					thread.start();
				}
				for(CRAThread t: threadList){
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				algoFinished = true;
				String status = counter.get()+"/"+numberOfAbstract+" in "+getDurationBreakdown(System.currentTimeMillis()-algoDuration);
				System.out.println(status);
				switch(measure.getSelectionModel().getSelectedIndex()){
					case 0:
						int k = 0;
						int count = 0;
						LinkedList<Element> finalList = new LinkedList<Element>();
						for(Record rec: targetList){
							for(Element e: rec.getProcessedNP()){
								count++;
								setStatus("Calculating average: "+Math.round(((float)count/wordCount.get())*100)+"%");
								if(!finalList.contains(e)){
									Element dummy = new Element(e.getNounPhrase());
									dummy.setInfluence(e.getInfluence());
									finalList.add(e);
								}else{
									Element temp = null;
									for(Element f: finalList){
										if(f.equals(e))
											temp = f;
									}
									float influence  = temp.getInfluence();
									if(!Float.isNaN(e.getInfluence()))
											influence += e.getInfluence();
									temp.setInfluence(influence);
								}
							}
						}
						for(Element e: finalList){
							float influence = e.getInfluence();
							e.setInfluence(influence/numberOfAbstract);
						}
						finalList.sort(new Comparator<Element>(){
							@Override
							public int compare(Element o1, Element o2) {
//								if(Float.isNaN(o1.getInfluence()))
//									return 1;
//								if(Float.isNaN(o2.getInfluence()))
//									return -1;
								return Float.compare(o2.getInfluence(),o1.getInfluence());
							}
						});
						k = 1;
						for(Element e: finalList){
							print += k+". "+e.getNounPhrase()+": "+e.getInfluence()+"\n";
							NounTableClass listItem = new NounTableClass(k, e.getNounPhrase(), Float.toString(e.getInfluence()));
							mainApp.getNounData().add(listItem);
							k++;
							if(k>printQuantity.getValue())
								break;
						}
						LinkedList<Record> sortHelper = new LinkedList<Record>();
						count = 0;
						for(Record rec: targetList){
							count ++;
							setStatus("Calculating Resonance: "+Math.round(((float)count/targetList.size())*100)+"%");
							sortHelper.add(rec);
							for(Element e: finalList){
								for(Element re: rec.getProcessedNP()){
									if(e.equals(re)){
										if(!Float.isNaN(re.getInfluence())&&!Float.isNaN(e.getInfluence())){
											float resonance = rec.getResonance();
											resonance += e.getInfluence()*re.getInfluence();
											rec.setResonance(resonance);
										}
									}
								}
							}
						}
						// Weighted Resonance
						if(weightedResonance.isSelected()){
							float sum = 0f;
							for(Element e: finalList){
								sum += Math.pow(e.getInfluence(), 2);
							}
							for(Record rec: sortHelper){
								float recSum = 0f;
								for(Element e: rec.getProcessedNP()){
									recSum += Math.pow(e.getInfluence(), 2);
								}
								if(((float)Math.sqrt(sum*recSum))>0){
									float resonance = rec.getResonance();
									resonance = resonance / (float)Math.sqrt(sum*recSum);
									rec.setResonance(resonance);
								}
							}
						}
						sortHelper.sort(new Comparator<Record>(){
							@Override
							public int compare(Record arg0, Record arg1) {
//								if(Float.isNaN(arg0.getResonance()))
//									return 1;
//								if(Float.isNaN(arg1.getResonance()))
//									return -1;
								return Float.compare(arg1.getResonance(),arg0.getResonance());
							}
						});
						k=1;
						for(Record rec: sortHelper){
							String authors ="";
							for(String s: rec.getAuthors()){
								if(authors.length()==0)
									authors += s;
								else
									authors +=", "+s;
							}
							ResonanceTableClass tempRec = new ResonanceTableClass(k, rec.getTitle(), Float.toString(rec.getResonance()), rec.getUrlID(), rec.getDb(), authors,rec.getAbstractText());
							mainApp.getResonanceData().add(tempRec);
							k++;
						}
						
						break;
					case 1:
						finalList = new LinkedList<Element>();
						HashMap<String, Float> avgHelper = new HashMap<String, Float>();
						for(Record abstr: targetList){
							for(Element e: abstr.getProcessedNP()){
								if(!finalList.contains(e)){
									finalList.add(e);
									avgHelper.put(e.getNounPhrase(), (float)e.getNeighbour().size());
								}else{
									float temp = avgHelper.get(e.getNounPhrase());
									temp += e.getNeighbour().size();
									avgHelper.put(e.getNounPhrase(), temp);
								}
							}
						}
						for(Element e: finalList){
//							System.out.println(avgHelper.get(e.getNounPhrase()));
							float avg = avgHelper.get(e.getNounPhrase())/numberOfAbstract;
//							System.out.println(avg);
							avgHelper.put(e.getNounPhrase(), avg);
						}
						finalList.sort(new Comparator<Element>(){

							@Override
							public int compare(Element arg0, Element arg1) {
								return avgHelper.get(arg1.getNounPhrase()).compareTo(avgHelper.get(arg0.getNounPhrase()));
							}
							
						});
						k = 1;
						for(Element e: finalList){
							print += k+". "+e.getNounPhrase()+": "+avgHelper.get(e.getNounPhrase())+"\n";	
							NounTableClass listItem = new NounTableClass(k, e.getNounPhrase(), Float.toString(avgHelper.get(e.getNounPhrase())));
							mainApp.getNounData().add(listItem);
							k++;
							if(k>printQuantity.getValue())
								break;
						}
						
						
						break;

				}
				
				
				System.out.println(print);
				setStatus(numberOfAbstract+" abstracts analyzed in "+getDurationBreakdown(System.currentTimeMillis()-algoDuration));
				System.out.println(numberOfAbstract+" abstracts analyzed in "+getDurationBreakdown(System.currentTimeMillis()-algoDuration));
				System.out.println("Total time to tag "+beforeMergeCount.get()+" words: "+LinkFilterThread.getTotalTagTime()+"ms split across "+Runtime.getRuntime().availableProcessors()+" Threads equals "+(LinkFilterThread.getTotalTagTime()/Runtime.getRuntime().availableProcessors())+"ms per Thread on average.\nAfter Merge for each individual abstract there were "+wordCount.get()+" words in total left.");
				analyseButton.setDisable(false);
			}
			
		});
		m.start();
	}
	
	@FXML
	private void handleCancle(){
		//TODO do this
	}
	
	public void setStatus(String text){
	     Platform.runLater(new Runnable() {
	         @Override public void run() {
	        	 statusLabel.setText(text);
	         }
	       });
	}
	
	/**
     * Convert a millisecond duration to a string format
     * 
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append("d ");
        sb.append(hours);
        sb.append("h ");
        sb.append(minutes);
        sb.append("m ");
        sb.append(seconds);
        sb.append("s ");

        return(sb.toString());
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
				"Degree centrality"
				);
		measure.setItems(measureContent);
		measure.getSelectionModel().selectFirst();
		
		IntegerSpinnerValueFactory factory = new IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,1000,1);
		printQuantity.setValueFactory(factory);
		
		analyseButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if(mainApp.getFile()!=null){
		    		handleAnalyse();
		    	}else{
		            // Nothing selected.
		            Alert alert = new Alert(AlertType.WARNING);
		            alert.initOwner(mainApp.getPrimaryStage());
		            alert.setTitle("No File");
		            alert.setHeaderText("No File Selected");
		            alert.setContentText("Please select a file.");

		            alert.showAndWait();
		    	}
		    		
		    }
		});
		
		influencePositionColumn.setCellValueFactory(cellData -> cellData.getValue().getPosition());
		influenceNounPhraseColumn.setCellValueFactory(cellData -> cellData.getValue().getNounPhrase());
		influenceAverageColumn.setCellValueFactory(cellData -> cellData.getValue().getAverage());
		
		resonancePositionColumn.setCellValueFactory(cellData -> cellData.getValue().getPosition());
		resonancenTextNameColumn.setCellValueFactory(cellData -> cellData.getValue().getTextName());
		resonancenResonanceColumn.setCellValueFactory(cellData -> cellData.getValue().getResonance());
		
		resonanceTable.setRowFactory( tv -> {
			    TableRow<ResonanceTableClass> row = new TableRow<>();
			    row.setOnMouseClicked(event -> {
			        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
			        	ResonanceTableClass rowData = row.getItem();
			        	mainApp.showRecordDetail(rowData);
			        }
			    });
			    return row ;
			});
		
//		IntegerSpinnerValueFactory spinnerfactory = new IntegerSpinnerValueFactory(1,Integer.MAX_VALUE, 30, 1);
		
		//Load Tokenizers
		new Thread(new Runnable() {
            @Override
            public void run() {
        		NPFilter npFilter = new NPFilter();
        		npFilter.OpenNlpTagger("");
        		NPFilter.stanfordTagger = new MaxentTagger("english-left3words-distsim.tagger");
        }}).start();

	}
	
	public int getLibraryModel(){
		return library.getSelectionModel().getSelectedIndex();
	}
	
	public int getMeasureModel(){
		return measure.getSelectionModel().getSelectedIndex();
	}

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		influenceTable.setItems(mainApp.getNounData());
		resonanceTable.setItems(mainApp.getResonanceData());
	}

}
