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
import cra.APSP.DijkstraRec;
import cra.model.Element;
import cra.model.PathSet;
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
	private CheckBox printNeighbors;
	
	@FXML
	private Label filePath;
	
	@FXML
	private ComboBox<String> library;
	
	@FXML
	private ComboBox<String> measure;
	
	private MainApp mainApp;
	
	public static int counter = 0;
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
//        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter(
//                "TXT files (*.txt)", "*.txt");        
//        fileChooser.getExtensionFilters().add(txtFilter);

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
		//TODO REMOVE THIS BEFORE SUBMISSION
//		NPFilter test = new NPFilter();
//		test.test("Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper,");
		

		ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>> nounPhrases = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Element>>();
		System.out.print("Tagging and linking ... ");
		long start = System.currentTimeMillis();
		ConcurrentLinkedQueue<String> abstracts = new ConcurrentLinkedQueue<String>();
		abstracts = mainApp.getAbstracts();
		
		
		
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
			mergeTarget.setId(id);;
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
					counter++;
				}
				System.out.println("Total: "+counter);
				algoFinished = true;
	
				int k = 0;
				for(Element e: mergeList){
					for(PathSet p: e.shortestPaths){
						System.out.print(p.getSource().getNounPhrase()+"=>"+p.getTarget().getNounPhrase()+": ");
						for(Element g: p.getPath()){
							System.out.print(g.getNounPhrase()+", ");
						}
						System.out.println("");
						k++;
						if(k>100)
							break;
					}
					if(k>100)
						break;
				}
				break;
			case 1:
				k = 0;
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
					String testung = e.getNounPhrase()+": "+e.getNeighbour().size();
					System.out.println(testung);
					if(k>30)
						break;
					k++;
				}
				break;
			case 2:
				algoFinished = false;
				algoDuration = System.currentTimeMillis();
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
				ConcurrentLinkedQueue<Element> eList = new ConcurrentLinkedQueue<Element>();
				for(Element e: mergeList){
					eList.add(e);
				}
				LinkedList<DijkstraRec> dijkstraRec = new LinkedList<DijkstraRec>();
				for(int l = 0; l<Runtime.getRuntime().availableProcessors(); l++){
					DijkstraRec thread = new DijkstraRec(eList, this);
					thread.setName(Integer.toString(l));
					dijkstraRec.add(thread);
					thread.start();
				}
				System.out.print("Thread count: "+threadList.size()+" ... ");
				for(DijkstraRec t: dijkstraRec){
					try {
						t.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
//				DijkstraRec dijkstra = null;
//				for(Element e: mergeList){
//					dijkstra = new DijkstraRec();
//					dijkstra.computePaths(e);
//					HashMap<Element, Integer> overList = new HashMap<Element, Integer>();
//					int totalPaths = 0;
//					Element last = null;
//					if(!e.shortestPaths.isEmpty())
//						 last = e.shortestPaths.getFirst().getTarget();
//					for(PathSet path: e.shortestPaths){						
//						totalPaths++;
//						if(overList.get(path.getTarget())==null)
//							overList.put(path.getTarget(), 0);
//						Integer tempInt = overList.get(path.getTarget());
//						tempInt++;
//						overList.put(path.getTarget(), tempInt);
//						if(path.getTarget()!=last){
//							for(Element in: overList.keySet()){
//								float influence = in.getInfluence()+(overList.get(in)/totalPaths);
//								in.setInfluence(influence);
//							}
//							totalPaths = 0;
//							overList.clear();
//						}
//						last = path.getTarget();
//					}
//					e.shortestPaths.clear();
//					counter++;
//				}
				for(Element e: mergeList){
					float influence = e.getInfluence()/((mergeList.size()-1)*(mergeList.size()-2)/2);
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
				k = 0;
				for(Element e: mergeList){
					System.out.println(e.getNounPhrase()+": "+e.getInfluence());
					k++;
					if(k>50)
						break;
				}
				
				System.out.println("Total: "+counter);
				algoFinished = true;
				
				break;
			default:
				break;
	}
		
	


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
