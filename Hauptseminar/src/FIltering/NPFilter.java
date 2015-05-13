package FIltering;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.didion.jwnl.util.factory.Param;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.parser.Parse;

public class NPFilter {

	
    static Set<String> nounPhrases = new HashSet<String>();
    static String testSentence = "Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper. The lazy brown fox jumps over the fence. That fence was build by your mother.";
    static ParserModel chunkingModel;
    static SentenceModel sentenceModel;
    private static NPFilter instance = null;

    
    protected NPFilter(){
    	try {
            sentenceModel = new SentenceModel(new FileInputStream("en-sent.bin"));
			chunkingModel = new ParserModel(new FileInputStream("en-parser-chunking.bin"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public static NPFilter getInstance() {
        if(instance == null) {
           instance = new NPFilter();
        }
        return instance;
     }
    
    public static Set<String> GetNounPhrases(String input){
    	NPDetect(testSentence);
    	return nounPhrases;
    }
    
    public static void main(String args[]){
    	//NPFilter bla = new NPFilter();
    	getInstance();
    	GetNounPhrases(testSentence);
    }

    /**
     * 
     * @param input
     */
	private static void NPDetect(String input){
		try{
    		String[] sentences = SentenceTokenizer(testSentence);
    		//String[] sentences = sentenceTokenizer(input);
    		for(String s : sentences)
    			Parse(s);
    		for (String s : nounPhrases)
        		System.out.println(s);       	
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * parses a String, tags its elements
	 * @param sentence
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private static void Parse(String sentence) throws InvalidFormatException, IOException{
    	opennlp.tools.parser.Parser parser = ParserFactory.create(chunkingModel);
    	opennlp.tools.parser.Parse[] toParses = ParserTool.parseLine(sentence, parser, 1);
    	for(Parse p: toParses)
    		ExtractNPs(p);
    	/*for(Parse p: toParses)
    		p.show();*/
    }
    
	/***
	 * recursively loops through parse tree and extracts
	 * the elements that have been tagged as "NP" (or Noun Phrase)
	 * @param parse
	 */
	private static void ExtractNPs(Parse parse) {
	    if (parse.getType().equals("NP")) {
	         nounPhrases.add(parse.getCoveredText());
	    }
	    for (Parse child : parse.getChildren())
	    	ExtractNPs(child);
	}
	
    /**
     * uses OpenNLP to split sentences from a String
     * @param text
     * @return
     * @throws IOException
     */
	private static String[] SentenceTokenizer(String text)throws IOException{
        
        SentenceDetectorME sDetector = new SentenceDetectorME(sentenceModel);
        //is.close();
        return sDetector.sentDetect(text);
    }
	
}
