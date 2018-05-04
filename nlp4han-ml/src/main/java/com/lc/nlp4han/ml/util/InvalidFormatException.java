package com.lc.nlp4han.ml.util;

import java.io.IOException;

/**
 * This exception indicates that a resource violates the expected data format.
 */
public class InvalidFormatException extends IOException {

  private static final long serialVersionUID = 0;

  public InvalidFormatException() {
  }

  public InvalidFormatException(String message) {
    super(message);
  }

  public InvalidFormatException(Throwable t) {
    super();
    initCause(t);
  }

  public InvalidFormatException(String message, Throwable t) {
    super(message);
    initCause(t);
  }
}
