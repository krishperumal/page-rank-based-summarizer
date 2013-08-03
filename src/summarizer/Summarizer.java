package summarizer;

import java.io.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import java.lang.Math;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Summarizer { 

  int[] sentenceLengths;
  int totalLength=0; //No of tokens

  ArrayList<String> stopwords = new ArrayList<String>(Arrays.asList("a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"));  

  public boolean isStopWord(String word) {
    return stopwords.contains(word);   
  }
 
  public boolean isPunct(String token) {
    if(token.length() == 0) {
      return true;
    }
    else if(token.length() == 1) {
      switch(token.charAt(0)) {
	case '.':
	case ',':
	case '?':
	case '!':
	case ':':
	case ';':
	case '"':
	case '-':
	case '/':
	case '\'':
	case '`':
	case '|': return true;
	default: return false;
      }
    }
    else {
      return false;
    }
  }
  
  public String[] splitIntoSentences(String text) {
    InputStream modelIn;
    SentenceModel model;
    String sentences[]={""};
    try {
      modelIn = getClass().getResourceAsStream("en-sent.bin");
      model = new SentenceModel(modelIn);
      if (modelIn != null) {
           modelIn.close();
      }
      SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
      sentences = sentenceDetector.sentDetect(text);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sentences;
  }

  public float getSimilarityScore(String[] tokens1, int length1, String[] tokens2, int length2) {
    int count=0,i,j;
    for(i=0;i<length1;i++) {
      for(j=0;j<length2;j++) {
	//System.out.println("i:"+i+" j:"+j+" tokens1: "+tokens1[i]+" tokens2: "+tokens2[j]);
	if(tokens1[i].equals(tokens2[j])) {
	  count++;
	}
      }
    }
    float similarityScore=(float)count/(float)(Math.log((double)length1)+Math.log((double)length2));
    return similarityScore;
  }

  public float[][] constructSimilarityMatrix(String[][] tokens) {
    float[][] similarities = new float[100][100];
    int i,j;
    for(i=0;i<tokens.length;i++) {
      for(j=i+1;j<tokens.length;j++) {
	similarities[i][j]=getSimilarityScore(tokens[i],sentenceLengths[i],tokens[j],sentenceLengths[j]);
      }
    }
    return similarities;
  }

  public float[] scoreSentences(String[] sentences) {
    float[] scores;
    float[] positionScores, lengthScores, tfs, tss, pageRankScores;
    float[] sentenceSimilarities;
    HashMap hm=new HashMap();
    String[] tempTokens, validTokens;
    int tokenNo;
    int noOfSentences=sentences.length;
    String[][] tokens = new String[noOfSentences][];
    scores=new float[noOfSentences];
    positionScores=new float[noOfSentences];
    lengthScores=new float[noOfSentences];
    tfs=new float[noOfSentences];
    tss=new float[noOfSentences];
    pageRankScores=new float[noOfSentences];
    sentenceLengths=new int[noOfSentences];
    int noOfTokens=0;
    InputStream modelIn;
    TokenizerModel model;
    try {
      modelIn = getClass().getResourceAsStream("en-token.bin");
      model = new TokenizerModel(modelIn);
      Tokenizer tokenizer = new TokenizerME(model);
      int count,i,j,k,l;
      for(i=0;i<noOfSentences;i++) {
    	tempTokens = tokenizer.tokenize(sentences[i]);
	//tokens[i] = tokenizer.tokenize(sentences[i]);
	sentenceLengths[i]=0;
	//only valid tokens
	validTokens=new String[tempTokens.length];
	tokenNo=0;
	for(k=0;k<tempTokens.length;k++) {
	  if(!isPunct(tempTokens[k]) && !isStopWord(tempTokens[k])) {
	    validTokens[tokenNo++]=tempTokens[k];
	  }
	}
	sentenceLengths[i]=tokenNo;
	System.out.println("sentence "+i+": "+sentenceLengths[i]);
	totalLength+=sentenceLengths[i];
	tokens[i]=validTokens;
	for(j=0;j<sentenceLengths[i];j++) {
	  if(tokens[i][j].length() > 1) {
	    noOfTokens++;
	    if(!hm.containsKey(tokens[i][j])) {
	      hm.put(tokens[i][j],1);
	    }
	    else {
	     count=(int)Integer.parseInt(hm.get(tokens[i][j]).toString());
	     hm.put(tokens[i][j],count+1);
	    }
	  }
	}
      }
      float[][] similarityMatrix = constructSimilarityMatrix(tokens);
      if(modelIn != null) {
	modelIn.close();
      }
      for(i=0;i<noOfSentences;i++) {
	positionScores[i] = 1-(float)((float)(i+1)/(float)noOfSentences); System.out.println("Pos score of "+i+" = "+positionScores[i]);
        lengthScores[i] = (float)sentenceLengths[i]/(float)totalLength; System.out.println("Length score of "+i+" = "+lengthScores[i]);
	count = 0;
	for(j=0;j<sentenceLengths[i];j++) {
	  if(hm.containsKey(tokens[i][j])) {
	    count = count + (int)Integer.parseInt(hm.get(tokens[i][j]).toString());
	  }
	}
	tfs[i] = (float)count/(float)sentenceLengths[i]; System.out.println("Term Freq Score of "+i+" = "+tfs[i]);
	scores[i] = positionScores[i] + lengthScores[i] + tfs[i];
      }
      for(i=0;i<noOfSentences;i++) {
	pageRankScores[i] = 0.15f*scores[i];
	for(j=0;j<noOfSentences;j++) {
	  if(i!=j) {
	    pageRankScores[i] = pageRankScores[i] + 0.85f*scores[i]*similarityMatrix[i<j?i:j][i>j?i:j];
	  }
	}
	System.out.println("PageRank Score of "+i+" = "+pageRankScores[i]);
      }
    }
    catch (IOException e) {
       e.printStackTrace();
    }
    return pageRankScores;
  }

  
  public String  constructHighlightedSummary(String[] sentences,float[] scores,int percentage) {
    StringBuilder highlightedSummary = new StringBuilder("");
    final Integer[] idx = new Integer[sentences.length];
    final float[] data = scores;
    int i;
    for(i=0;i<sentences.length;i++) {
      idx[i] = i;
    }
    Arrays.sort(idx, new Comparator<Integer>() {
    @Override public int compare(final Integer o1, final Integer o2) {
        return Float.compare(data[o1], data[o2]);
    }
    });
    int currentLength = 0;
    for(i=idx.length-1;i>=0;i--) {
      currentLength = currentLength + sentenceLengths[idx[i]];
      //System.out.println("Sentence "+i+": "+sentences[i]);
      if(((float)currentLength/totalLength)*100 >= percentage) {
	break;
      }
    }
    int[] summaryIndices = new int[idx.length-(i+1)];
    for(i=0;i<summaryIndices.length;i++) {
      summaryIndices[i]=idx[idx.length-(i+1)];
    }
    Arrays.sort(summaryIndices);
    int summary_sent_index=0;
    for(i=0;i<sentences.length;i++) {
      if(summary_sent_index < summaryIndices.length && i==summaryIndices[summary_sent_index]) {
        highlightedSummary.append(" <span class=\"highlightedSummary\">"+sentences[summaryIndices[summary_sent_index++]]+"</span>");
      }
      else {
        highlightedSummary.append(" "+sentences[i]);
      }
    }
    return highlightedSummary.toString();
  }

  public String  constructSummary(String[] sentences,float[] scores,int percentage) {
    StringBuilder summary = new StringBuilder("");
    final Integer[] idx = new Integer[sentences.length];
    final float[] data = scores;
    int i;
    for(i=0;i<sentences.length;i++) {
      idx[i] = i;
    }
    Arrays.sort(idx, new Comparator<Integer>() {
    @Override public int compare(final Integer o1, final Integer o2) {
        return Float.compare(data[o1], data[o2]);
    }
    });
    int currentLength = 0;
    for(i=idx.length-1;i>=0;i--) {
       currentLength = currentLength + sentenceLengths[idx[i]];
       //System.out.println("Sentence "+i+": "+sentences[i]);
       if(((float)currentLength/totalLength)*100 >= percentage) {
         break;
       }
     }
     int[] summaryIndices = new int[idx.length-(i+1)];
     for(i=0;i<summaryIndices.length;i++) {
       summaryIndices[i]=idx[idx.length-(i+1)];
    }
    Arrays.sort(summaryIndices);
    for(i=0;i<summaryIndices.length;i++) {
      summary.append(sentences[summaryIndices[i]]+" ");
    }
    return summary.toString();
  }
	
  public String getHighlightedSummary(String text,int percentage)
  {
    String[] sentences=splitIntoSentences(text);
    float[] scores=scoreSentences(sentences);
    String highlightedSummary=constructHighlightedSummary(sentences,scores,percentage);
    return highlightedSummary;
  }

  public String getSummary(String text, int percentage) {
    String[] sentences=splitIntoSentences(text);
    float[] scores=scoreSentences(sentences);
    String summary=constructSummary(sentences,scores,percentage);
    return summary;
  }
  
  public static void main(String[] args) {
    if(args.length < 1 || args.length > 2) {
      System.out.println("Usage: java Summarizer <input_file_name> <percentage>(default: 50)");
      System.exit(-1);
    }
    StringBuilder text=new StringBuilder("");
    try {
      FileInputStream fstream = new FileInputStream(args[0]);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String str;
      while ((str = br.readLine()) != null) {
	text.append(str);
      }
      in.close();
    } catch (Exception e) {
      System.err.println(e);
    }
    Summarizer summarizer=new Summarizer();
    int percentage;
    if(args.length == 2) {
      percentage = Integer.parseInt(args[1]);
    }
    else {
      percentage = 50; //default
    }
    String summary=summarizer.getSummary(text.toString(),percentage);
    System.out.println(summary);
  }
}   
