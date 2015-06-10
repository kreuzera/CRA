package filtering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.parser.Parse;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class NPFilter {

	
    static Set<String> nounPhrases = new HashSet<String>();
    static String testSentence = "the quick brown fox jumps over the lazy dog. Half an ancient silver fifty cent piece, several quotations from John Donne's sermons written incorrectly, each on a separate piece of transparent tissue-thin paper. The lazy brown fox jumps over the fence. That fence was build by your mother.";
    static ParserModel chunkingModel;
    static SentenceModel sentenceModel;
    static POSModel taggerModel;
    static TokenizerModel tokenizerModel;
    private static NPFilter instance = null;

    static double loadInTime;
    static double parseTime; 
    private ArrayList<String[]> wordList = new ArrayList<String[]>();
    
   
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
    /**
     * returns an ArrayList of Tagged Words from a sentence
     * For a documentation of tags see 
     * http://blog.dpdearing.com/2011/12/opennlp-part-of-speech-pos-tags-penn-english-treebank/
     * @param sentence
     * @return
     */
    public ArrayList<String[]> GetTaggedWordsFromSentence(String sentence){
		try {
			wordList.clear();
			wordList = WordTagger(WordTokenizer(sentence));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return wordList;
    	
    }    

    
    

    /**
     * 
     * @param input
     */
	private static void NPDetect(String input){
		try{
    		String[] sentences = SentenceTokenizer(testSentence);
    		for(String s : sentences)
    			Parse(s);
    		//for (String s : nounPhrases)
        		//System.out.println(s);       	
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
    	if(chunkingModel == null)
			chunkingModel = new ParserModel(new FileInputStream("en-parser-chunking.bin"));
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
	    for (Parse child : parse.getChildren()){
	    	ExtractNPs(child);
	    }
	}

	public ArrayList<String[]> WordTagger(String[] words) throws IOException {
		if(taggerModel==null)
			taggerModel = new POSModelLoader().load(new File("en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(taggerModel);
		String[] tags = null;
		tags = tagger.tag(words);
			for(int i= 0; i< tags.length; i++){
				String[] temp = {tags[i], words[i]};
				wordList.add(temp);
			}
		return wordList;
		}
	
    /**
     * uses OpenNLP to split sentences from a String
     * @param text
     * @return
     * @throws IOException
     */
	private static String[] SentenceTokenizer(String text)throws IOException{
		if(sentenceModel==null)
			sentenceModel = new SentenceModel(new FileInputStream("en-sent.bin"));
        SentenceDetectorME sDetector = new SentenceDetectorME(sentenceModel);
        return sDetector.sentDetect(text);
    }
	
	public static String[] WordTokenizer(String sentence) throws InvalidFormatException, IOException {
		if(tokenizerModel==null)
			tokenizerModel = new TokenizerModel(new FileInputStream("en-token.bin"));
		Tokenizer tokenizer = new TokenizerME(tokenizerModel);
		return tokenizer.tokenize(sentence);
	}
	
}
