package com.lc.nlp4han.ml.maxent.gis;

import java.io.File;
import java.io.IOException;

/**
 * This class works exactly like the SuffisSensitiveGISModelReader except that it
 * attempts to pool all context strings.  This is useful when loading models which
 * share many context strings.
 *
 */
public class PooledGISModelReader extends SuffixSensitiveGISModelReader {

  /**
   * A reader for GIS models which inspects the filename and invokes the
   * appropriate GISModelReader depending on the filename's suffixes.
   *
   * <p>The following assumption are made about suffixes:
   * <ul>
   *    <li>.gz  --&gt; the file is gzipped (must be the last suffix)</li>
   *    <li>.txt --&gt; the file is plain text</li>
   *    <li>.bin --&gt; the file is binary</li>
   * </ul>
   *
   * @param f
   * @throws IOException
   */
  public PooledGISModelReader(File f) throws IOException {
    super(f);
  }

  public String readUTF() throws IOException {
    return super.readUTF().intern();
  }
}
