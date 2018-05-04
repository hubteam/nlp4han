package com.lc.nlp4han.ml.naivebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.model.PlainTextFileDataReader;

public class PlainTextNaiveBayesModelReader extends NaiveBayesModelReader {

  /**
   * Constructor which directly instantiates the BufferedReader containing
   * the model contents.
   *
   * @param br The BufferedReader containing the model information.
   */
  public PlainTextNaiveBayesModelReader(BufferedReader br) {
    super(new PlainTextFileDataReader(br));
  }

  /**
   * Constructor which takes a File and creates a reader for it. Detects
   * whether the file is gzipped or not based on whether the suffix contains
   * ".gz".
   *
   * @param f The File in which the model is stored.
   */
  public PlainTextNaiveBayesModelReader(File f) throws IOException {
    super(f);
  }
}
