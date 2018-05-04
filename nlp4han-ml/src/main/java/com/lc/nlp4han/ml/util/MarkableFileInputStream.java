package com.lc.nlp4han.ml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A markable File Input Stream.
 */
class MarkableFileInputStream extends InputStream {

  private FileInputStream in;

  private long markedPosition = -1;
  private IOException markException;

  MarkableFileInputStream(File file) throws FileNotFoundException {
    in = new FileInputStream(file);
  }

  @Override
  public synchronized void mark(int readlimit) {
    try {
      markedPosition = in.getChannel().position();
    } catch (IOException e) {
      markedPosition = -1;
    }
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  private void throwMarkExceptionIfOccured() throws IOException {
    if (markException != null) {
      throw markException;
    }
  }

  @Override
  public synchronized void reset() throws IOException {
    throwMarkExceptionIfOccured();

    if (markedPosition >= 0) {
      in.getChannel().position(markedPosition);
    }
    else {
      throw new IOException("Stream has to be marked before it can be reset!");
    }
  }

  @Override
  public int read() throws IOException {
    return in.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return in.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return in.read(b, off, len);
  }
}
