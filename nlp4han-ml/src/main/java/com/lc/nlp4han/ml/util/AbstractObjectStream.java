package com.lc.nlp4han.ml.util;

import java.io.IOException;

public class AbstractObjectStream<T> implements ObjectStream<T> {

  private final ObjectStream<T> stream;

  protected AbstractObjectStream(ObjectStream<T> stream) {
    this.stream = stream;
  }

  @Override
  public T read() throws IOException {
    return stream.read();
  }

  @Override
  public void reset() throws IOException, UnsupportedOperationException {
    stream.reset();
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
