package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Allows repeated reads through a stream for certain types of model building.
 * Use {@code MockInputStreamFactory} MockInputStreamFactory for default
 * behavior.
 *
 */
public interface InputStreamFactory {

  InputStream createInputStream() throws IOException;
}