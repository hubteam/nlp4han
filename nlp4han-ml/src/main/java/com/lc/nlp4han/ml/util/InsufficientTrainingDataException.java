package com.lc.nlp4han.ml.util;

import java.io.IOException;

/**
 * This exception indicates that the provided training data is
 * insufficient to train the desired model.
 */
public class InsufficientTrainingDataException extends IOException {

  private static final long serialVersionUID = 0;

  public InsufficientTrainingDataException() {
  }

  public InsufficientTrainingDataException(String message) {
    super(message);
  }

  public InsufficientTrainingDataException(Throwable t) {
    super();
    initCause(t);
  }

  public InsufficientTrainingDataException(String message, Throwable t) {
    super(message);
    initCause(t);
  }
}
