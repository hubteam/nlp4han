package com.lc.nlp4han.ml.maxent.gis;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import com.lc.nlp4han.ml.model.AbstractModel;

/**
 * A writer for GIS models which inspects the filename and invokes the
 * appropriate GISModelWriter depending on the filename's suffixes.
 *
 * <p>The following assumption are made about suffixes:
 * <ul>
 *    <li>.gz  --&gt; the file is gzipped (must be the last suffix)</li>
 *    <li>.txt --&gt; the file is plain text</li>
 *    <li>.bin --&gt; the file is binary</li>
 * </ul>
 */
public class SuffixSensitiveGISModelWriter extends GISModelWriter {
  private final GISModelWriter suffixAppropriateWriter;

  /**
   * Constructor which takes a GISModel and a File and invokes the
   * GISModelWriter appropriate for the suffix.
   *
   * @param model The GISModel which is to be persisted.
   * @param f The File in which the model is to be stored.
   */
  public SuffixSensitiveGISModelWriter (AbstractModel model, File f)
  throws IOException {

    super (model);

    OutputStream output;
    String filename = f.getName();

    // handle the zipped/not zipped distinction
    if (filename.endsWith(".gz")) {
      output = new GZIPOutputStream(new FileOutputStream(f));
      filename = filename.substring(0,filename.length()-3);
    }
    else {
      output = new DataOutputStream(new FileOutputStream(f));
    }

    // handle the different formats
    if (filename.endsWith(".bin")) {
      suffixAppropriateWriter =
        new BinaryGISModelWriter(model,
            new DataOutputStream(output));
    }
    else { // default is ".txt"
      suffixAppropriateWriter =
        new PlainTextGISModelWriter(model,
            new BufferedWriter(new OutputStreamWriter(output)));
    }
  }

  public void writeUTF (String s) throws java.io.IOException {
    suffixAppropriateWriter.writeUTF(s);
  }

  public void writeInt (int i) throws java.io.IOException {
    suffixAppropriateWriter.writeInt(i);
  }

  public void writeDouble (double d) throws java.io.IOException {
    suffixAppropriateWriter.writeDouble(d);
  }

  public void close () throws java.io.IOException {
    suffixAppropriateWriter.close();
  }
}
