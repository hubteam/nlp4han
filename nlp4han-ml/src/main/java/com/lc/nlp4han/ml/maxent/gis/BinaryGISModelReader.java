package com.lc.nlp4han.ml.maxent.gis;

import java.io.DataInputStream;

import com.lc.nlp4han.ml.model.BinaryFileDataReader;

/**
 * A reader for GIS models stored in binary format.
 */
public class BinaryGISModelReader extends GISModelReader {

  /**
   * Constructor which directly instantiates the DataInputStream containing the
   * model contents.
   *
   * @param dis
   *          The DataInputStream containing the model information.
   */
  public BinaryGISModelReader(DataInputStream dis) {
    super(new BinaryFileDataReader(dis));
  }
}
