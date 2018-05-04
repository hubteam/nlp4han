package com.lc.nlp4han.ml.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A factory that creates {@link MarkableFileInputStream} from a {@link File}
 */
public class MarkableFileInputStreamFactory implements InputStreamFactory {

  private File file;

  public MarkableFileInputStreamFactory(File file) throws FileNotFoundException {
    if(!file.exists()) {
      throw new FileNotFoundException("File '" + file + "' cannot be found");
    }
    this.file = file;
  }

  @Override
  public InputStream createInputStream() throws IOException {
    return new MarkableFileInputStream(file);
  }
}
