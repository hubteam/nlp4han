package com.lc.nlp4han.ml.maxent.quasinewton;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.lc.nlp4han.ml.model.AbstractModel;


public class ObjectQNModelWriter extends QNModelWriter {

  protected ObjectOutputStream output;

  /**
   * Constructor which takes a GISModel and a ObjectOutputStream and prepares
   * itself to write the model to that stream.
   *
   * @param model The GISModel which is to be persisted.
   * @param dos The stream which will be used to persist the model.
   */
  public ObjectQNModelWriter(AbstractModel model, ObjectOutputStream dos) {
    super(model);
    output = dos;
  }

  public void writeUTF(String s) throws IOException {
    output.writeUTF(s);
  }

  public void writeInt(int i) throws IOException {
    output.writeInt(i);
  }

  public void writeDouble(double d) throws IOException {
    output.writeDouble(d);
  }

  public void close() throws IOException {
    output.flush();
    output.close();
  }
}
