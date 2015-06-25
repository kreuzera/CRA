package cra.view;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import cra.MainApp;
import cra.APSP.Algorithmus;
import cra.APSP.Dijkstra;
import cra.model.Element;
import cra.model.PathSet;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
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
	private TextArea textArea;
	
	private MainApp mainApp;
	
	public int counter = 0;
	private static boolean algoFinished = false;
	private ConcurrentLinkedQueue<Thread> threadPList = new ConcurrentLinkedQueue<Thread>();
	
	
	
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
		analyseButton.setDisable(true);
		MainViewController controller = this;
		for(Thread thread: threadPList){
			if(thread!=null && thread.isAlive()){
				thread.interrupt();
				threadPList.remove(thread);
			}
		}
		Thread thread = new Thread(new Runnable() {
			
            @Override
            public void run() {
            	
				long totalTime = System.currentTimeMillis();
				textArea.setText("");
				//TODO REMOVE THIS BEFORE SUBMISSION
		//		NPFilter test = new NPFilter();
		//		test.test("Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper,");
				
		
				ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>>();
				System.out.print("Tagging and linking ... ");
				long start = System.currentTimeMillis();
				ConcurrentLinkedQueue<String> abstracts = new ConcurrentLinkedQueue<String>();
				abstracts = mainApp.getAbstracts();
				if(abstracts.size()==0){
					mainApp.loadXml(mainApp.getFile());
					abstracts = mainApp.getAbstracts();
				}
				
				
				LinkedList<LinkFilterThread> threadList = new LinkedList<LinkFilterThread>();
				for(int i = 0; i<Runtime.getRuntime().availableProcessors(); i++){
					LinkFilterThread thread = new LinkFilterThread(abstracts, nounPhrases, library.getSelectionModel().getSelectedIndex(), controller);
					thread.setName(Integer.toString(i));
					threadList.add(thread);
					threadPList.add(thread);
					thread.start();
				}
				System.out.print("Thread count: "+threadList.size()+" ... ");
				for(LinkFilterThread t: threadList){
					try {
						t.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
				System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms");
				// Merge
				System.out.print("Merging ...");
				start = System.currentTimeMillis();
				LinkedList<Element> mergeList = new LinkedList<Element>();
				int i = 0;
				int id = 0;
				for(ConcurrentLinkedQueue<Element> eList: nounPhrases.values()){
					Element mergeTarget = new Element(eList.peek().getNounPhrase());
					mergeTarget.setId(id);
					setStatus("Merging: "+id+"/"+nounPhrases.size());
					id++;
					if(eList!=null)
					for(Element e: eList){
						i++;
						for(Element n: e.getNeighbour()){
		
							if(!mergeTarget.getNeighbour().contains(n))
								mergeTarget.addNeighbour(n);
								n.getNeighbour().remove(e);
								n.addNeighbour(mergeTarget);
						}					
					}
					mergeList.add(mergeTarget);
				}
		
				System.out.println(" finished in "+(System.currentTimeMillis()-start)+"ms. "+i+" nouns merged into "+nounPhrases.size()+" nouns. ");
				System.out.println("Total time: "+(System.currentTimeMillis()-totalTime)+"ms. Printing in 5 Seconds");
		//		try {
		//			Thread.sleep(5000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
				String print ="";
				switch(measure.getSelectionModel().getSelectedIndex()){
					case 0:
						algoFinished = false;
						long algoDuration = System.currentTimeMillis();
						counter = 0;
						new Thread(new Runnable() {
				            @Override
				            public void run() {
				        		try {
				        			while(!algoFinished){
				        				System.out.println(counter+" in "+getDurationBreakdown(System.currentTimeMillis()-algoDuration));
				        				Thread.sleep(1000);        				
				        			}		
				        		} catch (InterruptedException e) {
				        			// TODO Auto-generated catch block
				        			e.printStackTrace();
				        		}
				        }}).start();
						Algorithmus algo = new Algorithmus();
						for(Element e: mergeList){
							algo.computePaths(e);
							for(Element reset: mergeList){
								reset.setMinDistance(Double.MAX_VALUE);
								reset.setPrevious(null);
							}
							counter++;
						}
						System.out.println("Total: "+counter);
						algoFinished = true;
			
						int k = 0;
						for(Element e: mergeList){
		//					if(e.getNounPhrase().equals("half")){
							for(PathSet p: e.shortestPaths){
								System.out.print(p.getSource().getNounPhrase()+"=>"+p.getTarget().getNounPhrase()+": ");
								for(Element g: p.getPath()){
									System.out.print(g.getNounPhrase()+", ");
								}
								System.out.println("");
								k++;
								if(k>printQuantity.getValue())
									break;
							}
		//					}
						}
						break;
					case 1:
						k = 1;
						mergeList.sort(new Comparator<Element>(){
							@Override
							public int compare(Element o1, Element o2) {
								if(o1.getNeighbour().size()>o2.getNeighbour().size())
									return -1;
								if(o1.getNeighbour().size()<o2.getNeighbour().size())
									return 1;
								return 0;
							}
							
						});
						
						for(Element e: mergeList){
							String testung = k+". "+e.getNounPhrase()+": "+e.getNeighbour().size();
							System.out.println(testung);
							print += testung+"\n";
							if(k>printQuantity.getValue())
								break;
							k++;
						}
						break;
					case 2:
						algoFinished = false;
						algoDuration = System.currentTimeMillis();
						counter = 0;
						System.out.println("");
						Thread countdown = new Thread(new Runnable() {
				            @Override
				            public void run() {
				        		try {
				        			String status ="";
				        			while(!algoFinished){
				        				status = "Calculating influence: "+counter+"/"+mergeList.size()+" in "+getDurationBreakdown(System.currentTimeMillis()-algoDuration);
				        				System.out.println(status);
				        				setStatus(status);
				        				Thread.sleep(1000);        				
				        			}		
				        		} catch (InterruptedException e) {
				        			// TODO Auto-generated catch block
				        			e.printStackTrace();
				        		}
				        }});
						countdown.start();
						ConcurrentLinkedQueue<Element> eList = new ConcurrentLinkedQueue<Element>();
						for(Element e: mergeList){
							eList.add(e);
						}
						LinkedList<Dijkstra> dijkstraRec = new LinkedList<Dijkstra>();
						for(int l = 0; l<Runtime.getRuntime().availableProcessors(); l++){
							Dijkstra thread = new Dijkstra(eList, controller);
							thread.setName(Integer.toString(l));
		//					thread.setPriority(Thread.MIN_PRIORITY);
							dijkstraRec.add(thread);
							threadPList.add(thread);
							thread.start();
						}
						System.out.println("Thread count: "+threadList.size()+" ... ");
						for(Dijkstra t: dijkstraRec){
							try {
								t.join();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
		//				k = 0;
		//				for(Element e: mergeList){
		//					if(e.getNounPhrase().equals("snippet")){					
		//					for(PathSet p: e.shortestPaths){
		//						System.out.print(p.getSource().getNounPhrase()+"=>"+p.getTarget().getNounPhrase()+": ");
		//						for(Element ep: p.getPath()){
		//							System.out.print(ep.getNounPhrase()+", ");
		//						}
		//						System.out.println("");
		//					}
		//					k++;
		//					if(k>100)
		//						break;
		//					}
		//				}
						for(Element e: mergeList){
							float influence = e.getInfluence()/((float)(mergeList.size()-1)*(mergeList.size()-2)/2);
							e.setInfluence(influence);					
						}
						mergeList.sort(new Comparator<Element>(){
		
							@Override
							public int compare(Element arg0, Element arg1) {
								if(arg0.getInfluence()>arg1.getInfluence())
									return -1;
								if(arg0.getInfluence()<arg1.getInfluence())
									return 1;
								return 0;
							}
							
						});
						k = 1;
						for(Element e: mergeList){
							System.out.println(k+". "+e.getNounPhrase()+": "+e.getInfluence());
							print += k+". "+e.getNounPhrase()+": "+e.getInfluence()+"\n";
							k++;
							if(k>printQuantity.getValue())
								break;
						}
						
						System.out.println("Total: "+counter);
						algoFinished = true;
						
						break;
					default:
						break;
				}
				textArea.setText(print);
				setStatus("done");
				analyseButton.setDisable(false);
            }});
		thread.start();
		threadPList.add(thread);
		
	}
	
	@FXML
	private void handleCancle(){
		for(Thread thread: threadPList){
			if(thread!=null && thread.isAlive()){
				thread.interrupt();
				threadPList.remove(thread);
			}
		}
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
				"Degree centrality",
				"Betweeness centrality (alternative)"
				);
		measure.setItems(measureContent);
		measure.getSelectionModel().selectFirst();
		
		IntegerSpinnerValueFactory factory = new IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,50,1);
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
		
		textArea.setEditable(false);
		
//		IntegerSpinnerValueFactory spinnerfactory = new IntegerSpinnerValueFactory(1,Integer.MAX_VALUE, 30, 1);
		
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
