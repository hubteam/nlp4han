package com.lc.nlp4han.ml.maxent.quasinewton;

import java.io.DataInputStream;

import com.lc.nlp4han.ml.model.BinaryFileDataReader;

/**
 * A reader for quasi-newton models stored in binary format.
 */
public class BinaryQNModelReader extends QNModelReader {

  /**
   * Constructor which directly instantiates the DataInputStream containing the
   * model contents.
   *
   * @param dis
   *          The DataInputStream containing the model information.
   */
  public BinaryQNModelReader(DataInputStream dis) {
    super(new BinaryFileDataReader(dis));
  }
}
