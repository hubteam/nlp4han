package com.lc.nlp4han.pos.word;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.InvalidFormatException;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * A stream filter which reads a sentence per line which contains
 * words and tags in word_tag format and outputs a {@link WordPOSSample} objects.
 */
public class WordTagSampleStream extends FilterObjectStream<String, WordPOSSample> {

  private static Logger logger = Logger.getLogger(WordTagSampleStream.class.getName());
  
  private String seperator;

  /**
   * Initializes the current instance.
   *
   * @param sentences the sentences
   */
  public WordTagSampleStream(ObjectStream<String> sentences, String sep) {
    super(sentences);
    this.seperator = sep;
  }

  /**
   * Parses the next sentence and return the next
   * {@link WordPOSSample} object.
   *
   * If an error occurs an empty {@link WordPOSSample} object is returned
   * and an warning message is logged. Usually it does not matter if one
   * of many sentences is ignored.
   *
   * TODO: An exception in error case should be thrown.
   */
  public WordPOSSample read() throws IOException {

    String sentence = samples.read();

    if (sentence != null) {
        if(sentence.length()==0)
            return new WordPOSSample(new String[]{}, new String[]{});
        
      WordPOSSample sample;
      try {
        sample = WordPOSSample.parse(sentence, seperator);
      } catch (InvalidFormatException e) {

        if (logger.isLoggable(Level.WARNING)) {
          logger.warning("Error during parsing, ignoring sentence: " + sentence);
        }

        sample = new WordPOSSample(new String[]{}, new String[]{});
      }

      return sample;
    }
    else {
      // sentences stream is exhausted
      return null;
    }
  }
}
