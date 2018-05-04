package com.lc.nlp4han.ml.naivebayes;

import com.lc.nlp4han.ml.model.Context;
import com.lc.nlp4han.ml.model.EvalParameters;

/**
 * Parameters for the evalution of a naive bayes classifier
 */
public class NaiveBayesEvalParameters extends EvalParameters {

  protected double[] outcomeTotals;
  protected long vocabulary;

  public NaiveBayesEvalParameters(Context[] params, int numOutcomes, double[] outcomeTotals, long vocabulary) {
    super(params, 0, 0, numOutcomes);
    this.outcomeTotals = outcomeTotals;
    this.vocabulary = vocabulary;
  }

  public double[] getOutcomeTotals() {
    return outcomeTotals;
  }

  public long getVocabulary() {
    return vocabulary;
  }

}
