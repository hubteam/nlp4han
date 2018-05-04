package com.lc.nlp4han.ml.maxent.quasinewton;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.maxent.gis.GISModelReader;
import com.lc.nlp4han.ml.model.Context;
import com.lc.nlp4han.ml.model.DataReader;


public class QNModelReader extends GISModelReader {
  public QNModelReader(DataReader dataReader) {
    super(dataReader);
  }

  public QNModelReader(File file) throws IOException {
    super(file);
  }

  @Override
  public void checkModelType() throws IOException {
    String modelType = readUTF();
    if (!modelType.equals("QN"))
      System.out.println("Error: attempting to load a " + modelType
          + " model as a MAXENT_QN model." + " You should expect problems.");
  }

  public QNModel constructModel() throws IOException {
    String[] outcomeLabels = getOutcomes();
    int[][] outcomePatterns = getOutcomePatterns();
    String[] predLabels = getPredicates();
    Context[] params = getParameters(outcomePatterns);

    return new QNModel(params, predLabels, outcomeLabels);
  }
}
