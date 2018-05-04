package com.lc.nlp4han.ml.util;

public interface EvaluationMonitor<T> {

  void correctlyClassified(T reference, T prediction);

  void missclassified(T reference, T prediction);

}
