package com.lc.nlp4han.ml.maxent.gis;

import java.io.ObjectInputStream;

import com.lc.nlp4han.ml.model.ObjectDataReader;

public class ObjectGISModelReader extends GISModelReader {

  protected ObjectInputStream input;

  /**
   * Constructor which directly instantiates the ObjectInputStream containing
   * the model contents.
   *
   * @param ois The DataInputStream containing the model information.
   */

  public ObjectGISModelReader(ObjectInputStream ois) {
    super(new ObjectDataReader(ois));
  }
}
