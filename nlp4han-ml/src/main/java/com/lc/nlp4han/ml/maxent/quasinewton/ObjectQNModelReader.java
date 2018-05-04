package com.lc.nlp4han.ml.maxent.quasinewton;

import java.io.ObjectInputStream;

import com.lc.nlp4han.ml.model.ObjectDataReader;


public class ObjectQNModelReader extends QNModelReader {

  public ObjectQNModelReader(ObjectInputStream ois) {
    super(new ObjectDataReader(ois));
  }
}
