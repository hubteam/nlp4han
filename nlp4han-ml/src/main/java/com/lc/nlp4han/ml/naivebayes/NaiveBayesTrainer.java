package com.lc.nlp4han.ml.naivebayes;

import java.io.IOException;

import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.DataIndexer;
import com.lc.nlp4han.ml.model.EvalParameters;
import com.lc.nlp4han.ml.model.MutableContext;
import com.lc.nlp4han.ml.util.AbstractEventTrainer;

/**
 * Trains models using the perceptron algorithm.  Each outcome is represented as
 * a binary perceptron classifier.  This supports standard (integer) weighting as well
 * average weighting as described in:
 * Discriminative Training Methods for Hidden Markov Models: Theory and Experiments
 * with the Perceptron Algorithm. Michael Collins, EMNLP 2002.
 */
public class NaiveBayesTrainer extends AbstractEventTrainer {

  public static final String NAIVE_BAYES_VALUE = "NAIVEBAYES";

  /**
   * Number of unique events which occurred in the event set.
   */
  private int numUniqueEvents;
  /**
   * Number of events in the event set.
   */
  private int numEvents;

  /**
   * Number of predicates.
   */
  private int numPreds;
  /**
   * Number of outcomes.
   */
  private int numOutcomes;
  /**
   * Records the array of predicates seen in each event.
   */
  private int[][] contexts;

  /**
   * The value associates with each context. If null then context values are assumes to be 1.
   */
  private float[][] values;

  /**
   * List of outcomes for each event i, in context[i].
   */
  private int[] outcomeList;

  /**
   * Records the num of times an event has been seen for each event i, in context[i].
   */
  private int[] numTimesEventsSeen;

  /**
   * Stores the String names of the outcomes.  The NaiveBayes only tracks outcomes
   * as ints, and so this array is needed to save the model to disk and
   * thereby allow users to know what the outcome was in human
   * understandable terms.
   */
  private String[] outcomeLabels;

  /**
   * Stores the String names of the predicates. The NaiveBayes only tracks
   * predicates as ints, and so this array is needed to save the model to
   * disk and thereby allow users to know what the outcome was in human
   * understandable terms.
   */
  private String[] predLabels;

  private boolean printMessages = true;

  public NaiveBayesTrainer() {
  }

  public boolean isSortAndMerge() {
    return false;
  }

  public AbstractModel doTrain(DataIndexer indexer) throws IOException {
    if (!isValid()) {
      throw new IllegalArgumentException("trainParams are not valid!");
    }

    return this.trainModel(indexer);
  }

  // << members related to AbstractSequenceTrainer

  public AbstractModel trainModel(DataIndexer di) {
    display("Incorporating indexed data for training...  \n");
    contexts = di.getContexts();
    values = di.getValues();
    numTimesEventsSeen = di.getNumTimesEventsSeen();
    numEvents = di.getNumEvents();
    numUniqueEvents = contexts.length;

    outcomeLabels = di.getOutcomeLabels();
    outcomeList = di.getOutcomeList();

    predLabels = di.getPredLabels();
    numPreds = predLabels.length;
    numOutcomes = outcomeLabels.length;

    display("done.\n");

    display("\tNumber of Event Tokens: " + numUniqueEvents + "\n");
    display("\t    Number of Outcomes: " + numOutcomes + "\n");
    display("\t  Number of Predicates: " + numPreds + "\n");

    display("Computing model parameters...\n");

    MutableContext[] finalParameters = findParameters();

    display("...done.\n");

    /* Create and return the model ****/
    return new NaiveBayesModel(finalParameters, predLabels, outcomeLabels);
  }

  private MutableContext[] findParameters() {

    int[] allOutcomesPattern = new int[numOutcomes];
    for (int oi = 0; oi < numOutcomes; oi++)
      allOutcomesPattern[oi] = oi;

    /* Stores the estimated parameter value of each predicate during iteration. */
    MutableContext[] params = new MutableContext[numPreds];
    for (int pi = 0; pi < numPreds; pi++) {
      params[pi] = new MutableContext(allOutcomesPattern, new double[numOutcomes]);
      for (int aoi = 0; aoi < numOutcomes; aoi++)
        params[pi].setParameter(aoi, 0.0);
    }

    EvalParameters evalParams = new EvalParameters(params, numOutcomes);

    double stepsize = 1;

    for (int ei = 0; ei < numUniqueEvents; ei++) {
      int targetOutcome = outcomeList[ei];
      for (int ni = 0; ni < this.numTimesEventsSeen[ei]; ni++) {
        for (int ci = 0; ci < contexts[ei].length; ci++) {
          int pi = contexts[ei][ci];
          if (values == null) {
            params[pi].updateParameter(targetOutcome, stepsize);
          } else {
            params[pi].updateParameter(targetOutcome, stepsize * values[ei][ci]);
          }
        }
      }
    }

    // Output the final training stats.
    trainingStats(evalParams);

    return params;

  }

  private double trainingStats(EvalParameters evalParams) {
    int numCorrect = 0;

    for (int ei = 0; ei < numUniqueEvents; ei++) {
      for (int ni = 0; ni < this.numTimesEventsSeen[ei]; ni++) {

        double[] modelDistribution = new double[numOutcomes];

        if (values != null)
          NaiveBayesModel.eval(contexts[ei], values[ei], modelDistribution, evalParams, false);
        else
          NaiveBayesModel.eval(contexts[ei], null, modelDistribution, evalParams, false);

        int max = maxIndex(modelDistribution);
        if (max == outcomeList[ei])
          numCorrect++;
      }
    }
    double trainingAccuracy = (double) numCorrect / numEvents;
    display("Stats: (" + numCorrect + "/" + numEvents + ") " + trainingAccuracy + "\n");
    return trainingAccuracy;
  }


  private int maxIndex(double[] values) {
    int max = 0;
    for (int i = 1; i < values.length; i++)
      if (values[i] > values[max])
        max = i;
    return max;
  }

  private void display(String s) {
    if (printMessages)
      System.out.print(s);
  }
}
