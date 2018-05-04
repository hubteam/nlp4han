package com.lc.nlp4han.ml.model;

import java.io.IOException;

public interface DataReader {

  public double readDouble() throws IOException;

  public int readInt() throws IOException;

  public String readUTF() throws IOException;
}
