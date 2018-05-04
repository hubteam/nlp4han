package com.lc.nlp4han.ml.model;

 /**
 * This class encapsulates the varibales used in producing probabilities from a model
 * and facilitaes passing these variables to the eval method.
 */
public class EvalParameters {

 /** Mapping between outcomes and paramater values for each context.
   * The integer representation of the context can be found using <code>pmap</code>.*/
  private Context[] params;
  /** The number of outcomes being predicted. */
  private final int numOutcomes;
  /** The maximum number of features fired in an event. Usually referred to as C.
   * This is used to normalize the number of features which occur in an event. */
  private double correctionConstant;

  /**  Stores inverse of the correction constant, 1/C. */
  private final double constantInverse;
  /** The correction parameter of the model. */
  private double correctionParam;

  /**
   * Creates a set of parameters which can be evaulated with the eval method.
   * @param params The parameters of the model.
   * @param correctionParam The correction parameter.
   * @param correctionConstant The correction constant.
   * @param numOutcomes The number of outcomes.
   */
  public EvalParameters(Context[] params, double correctionParam, double correctionConstant, int numOutcomes) {
    this.params = params;
    this.correctionParam = correctionParam;
    this.numOutcomes = numOutcomes;
    this.correctionConstant = correctionConstant;
    this.constantInverse = 1.0 / correctionConstant;
  }

  public EvalParameters(Context[] params, int numOutcomes) {
    this(params,0,0,numOutcomes);
  }

  /* (non-Javadoc)
   * @see opennlp.tools.ml.model.EvalParameters#getParams()
   */
  public Context[] getParams() {
    return params;
  }

  /* (non-Javadoc)
   * @see opennlp.tools.ml.model.EvalParameters#getNumOutcomes()
   */
  public int getNumOutcomes() {
    return numOutcomes;
  }

  public double getCorrectionConstant() {
    return correctionConstant;
  }

  public double getConstantInverse() {
    return constantInverse;
  }

  public double getCorrectionParam() {
    return correctionParam;
  }

  public void setCorrectionParam(double correctionParam) {
    this.correctionParam = correctionParam;
  }
}