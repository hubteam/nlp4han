
package com.lc.nlp4han.pos.word;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.lc.nlp4han.ml.util.InvalidFormatException;

/**
 * Represents an pos-tagged sentence.
 */
public class WordPOSSample {

  private List<String> sentence;

  private List<String> tags;

  private final String[][] additionalContext;
  
  public WordPOSSample(String sentence[], String tags[]) {
    this(sentence, tags, null);
  }

  public WordPOSSample(List<String> sentence, List<String> tags) {
    this(sentence, tags, null);
  }

  public WordPOSSample(List<String> sentence, List<String> tags,
      String[][] additionalContext) {
    this.sentence = Collections.unmodifiableList(sentence);
    this.tags = Collections.unmodifiableList(tags);

    checkArguments();
    String[][] ac;
    if (additionalContext != null) {
      ac = new String[additionalContext.length][];

      for (int i = 0; i < additionalContext.length; i++) {
        ac[i] = new String[additionalContext[i].length];
        System.arraycopy(additionalContext[i], 0, ac[i], 0,
            additionalContext[i].length);
      }
    } else {
      ac = null;
    }
    this.additionalContext = ac;
  }

  public WordPOSSample(String sentence[], String tags[],
      String[][] additionalContext) {
    this(Arrays.asList(sentence), Arrays.asList(tags), additionalContext);
  }

  private void checkArguments() {
    if (sentence.size() != tags.size()) {
      throw new IllegalArgumentException(
        "There must be exactly one tag for each token. tokens: " + sentence.size() +
            ", tags: " + tags.size());
    }

      if (sentence.contains(null)) {
        throw new IllegalArgumentException("null elements are not allowed in sentence tokens!");
      }
      if (tags.contains(null)) {
        throw new IllegalArgumentException("null elements are not allowed in tags!");
      }
  }

  public String[] getSentence() {
    return sentence.toArray(new String[sentence.size()]);
  }

  public String[] getTags() {
    return tags.toArray(new String[tags.size()]);
  }

  public String[][] getAddictionalContext() {
    return this.additionalContext;
  }

  @Override
  public String toString() {

    return toString("_");
  }
  
  public String toString(String sep) {

      StringBuilder result = new StringBuilder();

      for (int i = 0; i < getSentence().length; i++) {
        result.append(getSentence()[i]);
        result.append(sep);
        result.append(getTags()[i]);
        result.append(' ');
      }

      if (result.length() > 0) {
        // get rid of last space
        result.setLength(result.length() - 1);
      }

      return result.toString();
    }

  /**
   * 解析语料
   * @param sentenceString 句子
   *         读入的是树库文件是，这里的句子是括号表达式
   * @param sep 句子中的分割符
   * @param datatype 语料的类型，可以是pos，或者是树库文件
   *         如果是标准的词性标记文件，用pos表示
   *         如果是树库语料，用tree表示
   * @return
   * @throws InvalidFormatException
   */
  public static WordPOSSample parse(String sentenceString, String sep) throws InvalidFormatException {
 
	  String tokenTags[] = sentenceString.split("\\s");//WhitespaceTokenizer.INSTANCE.tokenize(sentenceString);
		    
	  String sentence[] = new String[tokenTags.length];   
	  String tags[] = new String[tokenTags.length];
	    
	  for (int i = 0; i < tokenTags.length; i++) {		    
			  
		  int split = tokenTags[i].lastIndexOf(sep);

		  if (split == -1) {		      
			  throw new InvalidFormatException("Cannot find \"" + sep + "\" inside token '" + tokenTags[i] + "'!");		    
		  }
			  
		  sentence[i] = tokenTags[i].substring(0, split);		      
		  tags[i] = tokenTags[i].substring(split+1);
	  }

	  return new WordPOSSample(sentence, tags);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj instanceof WordPOSSample) {
      WordPOSSample a = (WordPOSSample) obj;

      return Arrays.equals(getSentence(), a.getSentence())
          && Arrays.equals(getTags(), a.getTags());
    } else {
      return false;
    }
  }
}
