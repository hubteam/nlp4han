package com.lc.nlp4han.ml.maxent.quasinewton;

/**
 * Interface for a function
 */
public interface Function {

  int getDimension();

  double valueAt(double[] x);

  double[] gradientAt(double[] x);
}
