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
    
    public static Set<String> GetNounPhrases(String input){
    	NPDetect(testSentence);
    	return nounPhrases;
    }
    
    public static void main(String args[]){
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
    	InputStream is = new FileInputStream("en-parser-chunking.bin");
    	ParserModel model = new ParserModel(is);
    	opennlp.tools.parser.Parser parser = ParserFactory.create(model);
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
        InputStream is = new FileInputStream("en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sDetector = new SentenceDetectorME(model);
        is.close();
        return sDetector.sentDetect(text);
    }
	
}
