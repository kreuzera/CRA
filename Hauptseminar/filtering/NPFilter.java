package filtering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import net.didion.jwnl.JWNLException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.coref.mention.JWNLDictionary;
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
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;

public class NPFilter {

	private static Set<String> nounPhrases = new HashSet<String>();
	private static ParserModel chunkingModel;
	private static SentenceModel sentenceModel;
	private static POSModel taggerModel;
	private static TokenizerModel tokenizerModel;
	public static MaxentTagger stanfordTagger;
	private Morphology morph;
	private ArrayList<String[]> wordList = new ArrayList<String[]>();
	private Properties props = null;
	private StanfordCoreNLP pipe = null;

	/**
	 * returns an ArrayList of Tagged Words from a sentence For a documentation
	 * of tags see
	 * http://blog.dpdearing.com/2011/12/opennlp-part-of-speech-pos-tags
	 * -penn-english-treebank/
	 * 
	 * @param sentence
	 * @return
	 */
	
	public ArrayList<String[]> OpenNlpTagger(String sentence) {
		try {
			wordList.clear();
			sentence = sentence.replaceAll("[^\\dA-Za-z ]", "");
			wordList = WordTagger(WordTokenizer(sentence));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wordList;
	}
	
	public void ParseOutputTest(String sentence) {
		try {
			String[] sentences = SentenceTokenizer(sentence);
			for (String s : sentences)
				Parse(s);
			for (String s : nounPhrases)
				System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * parses a String, tags its elements
	 * 
	 * @param sentence
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private static void Parse(String sentence) throws InvalidFormatException,
			IOException {
		if (chunkingModel == null)
			chunkingModel = new ParserModel(new FileInputStream(
					"en-parser-chunking.bin"));
		opennlp.tools.parser.Parser parser = ParserFactory
				.create(chunkingModel);
		opennlp.tools.parser.Parse[] toParses = ParserTool.parseLine(sentence,
				parser, 1);

		for (Parse p : toParses)
			ExtractNPs(p);
		/*
		 * for(Parse p: toParses) p.show();
		 */
	}

	/***
	 * recursively loops through parse tree and extracts the elements that have
	 * been tagged as "NP" (or Noun Phrase)
	 * 
	 * @param parse
	 */
	private static void ExtractNPs(Parse parse) {
		if (parse.getType().equals("NP")) {
			nounPhrases.add(parse.getCoveredText());
		}
		for (Parse child : parse.getChildren()) {
			ExtractNPs(child);
		}
	}

	/**
	 * Tags words using OpenNLP Framework
	 * @param words
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String[]> WordTagger(String[] words) throws IOException {
		if (taggerModel == null)
			taggerModel = new POSModelLoader().load(new File(
					"en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(taggerModel);
		String[] tags = null;
		tags = tagger.tag(words);
		for (int i = 0; i < tags.length; i++) {
			String[] temp = { tags[i], words[i] };
			if (temp[0].equals("NNS") || temp[0].equals("NNPS")) {
				temp[1] = OpenNlpLemmatize(temp[1], temp[0]);
			}
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
	private static String[] SentenceTokenizer(String text) throws IOException {
		if (sentenceModel == null)
			sentenceModel = new SentenceModel(
					new FileInputStream("en-sent.bin"));
		SentenceDetectorME sDetector = new SentenceDetectorME(sentenceModel);
		return sDetector.sentDetect(text);
	}
	
	/**
	 * Splits words using OenNLP
	 * @param sentence
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private static String[] WordTokenizer(String sentence)
			throws InvalidFormatException, IOException {
		if (tokenizerModel == null)
			tokenizerModel = new TokenizerModel(new FileInputStream(
					"en-token.bin"));
		Tokenizer tokenizer = new TokenizerME(tokenizerModel);
		return tokenizer.tokenize(sentence);
	}
	/**
	 * 
	 * @param Text
	 * @return
	 */
	public ArrayList<String[]> StanfordNlpTagger(String Text) {
		if (stanfordTagger == null) {
			stanfordTagger = new MaxentTagger(
					"english-left3words-distsim.tagger");
		}
		wordList.clear();
//		Text = Text.replaceAll("\\u2010", "-");
		Text = Text.replaceAll("[^\\dA-Za-z ]", "");
		String[] sentences = StanfordSentenceSplitter(Text);
		for (String sentence : sentences) {
			String tagged = stanfordTagger.tagString(sentence);
			String[] words = tagged.split(" ");

			for (String word : words) {
				String[] temp = word.split("_");
				if (temp[1].equals("NNS") || temp[1].equals("NNPS")) {
					temp[0] = StanfordLemmatize(temp[0], temp[1]);
				}
				wordList.add(new String[] { temp[1], temp[0] });
			}
		}
		return wordList;
	}

	private String StanfordLemmatize(String word, String tag) {
		if (morph == null)
			morph = new Morphology();
		return morph.lemma(word, tag);
	}

	private String OpenNlpLemmatize(String word, String tag) {
		JWNLDictionary dic = null;
		String[] lemmas = null;
		if (dic == null){
			try {
				dic = new JWNLDictionary(".\\WordNet-2.0\\dict");
			} catch (IOException | JWNLException e) {
				e.printStackTrace();}
		}
		if (dic != null && !(word.matches("\\d+"))) {
			lemmas = dic.getLemmas(tag, word);
		}
		if(lemmas != null && lemmas.length > 0 )
			return lemmas[0];
		else return word;
	}

	private String[] StanfordSentenceSplitter(String Text) {
		if (props == null) {
			props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit");
		}
		if (pipe == null)
			pipe = new StanfordCoreNLP(props);

		Annotation importedText = new Annotation(Text);
		pipe.annotate(importedText);
		List<CoreMap> sentences = importedText.get(SentencesAnnotation.class);
		String[] temp = new String[sentences.size()];
		for (int i = 0; i < sentences.size(); i++) {
			temp[i] = sentences.get(i).toString();
		}
		return temp;
	}

}
