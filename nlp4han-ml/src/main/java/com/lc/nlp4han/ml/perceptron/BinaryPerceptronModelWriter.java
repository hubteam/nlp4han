package com.lc.nlp4han.ml.perceptron;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.lc.nlp4han.ml.model.AbstractModel;


/**
 * Model writer that saves models in binary format.
 */
public class BinaryPerceptronModelWriter extends PerceptronModelWriter {
  private DataOutputStream output;

  /**
   * Constructor which takes a GISModel and a File and prepares itself to
   * write the model to that file. Detects whether the file is gzipped or not
   * based on whether the suffix contains ".gz".
   *
   * @param model The GISModel which is to be persisted.
   * @param f The File in which the model is to be persisted.
   */
  public BinaryPerceptronModelWriter (AbstractModel model, File f) throws IOException {

    super(model);

    if (f.getName().endsWith(".gz")) {
      output = new DataOutputStream(
          new GZIPOutputStream(new FileOutputStream(f)));
    }
    else {
      output = new DataOutputStream(new FileOutputStream(f));
    }
  }

  /**
   * Constructor which takes a GISModel and a DataOutputStream and prepares
   * itself to write the model to that stream.
   *
   * @param model The GISModel which is to be persisted.
   * @param dos The stream which will be used to persist the model.
   */
  public BinaryPerceptronModelWriter (AbstractModel model, DataOutputStream dos) {
    super(model);
    output = dos;
  }

  public void writeUTF (String s) throws java.io.IOException {
    output.writeUTF(s);
  }

  public void writeInt (int i) throws java.io.IOException {
    output.writeInt(i);
  }

  public void writeDouble (double d) throws java.io.IOException {
    output.writeDouble(d);
  }

  public void close () throws java.io.IOException {
    output.flush();
    output.close();
  }

}
