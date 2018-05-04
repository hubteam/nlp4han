package com.lc.nlp4han.ml.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Reads a plain text file and return each line as a <code>String</code> object.
 */
public class PlainTextByLineStream implements ObjectStream<String> {

  private final FileChannel channel;
  private final String encoding;

  private InputStreamFactory inputStreamFactory;

  private BufferedReader in;

  public PlainTextByLineStream(InputStreamFactory inputStreamFactory, String charsetName) throws IOException {
    this(inputStreamFactory, Charset.forName(charsetName));
  }

  public PlainTextByLineStream(InputStreamFactory inputStreamFactory, Charset charset) throws IOException {
    this.inputStreamFactory = inputStreamFactory;
    this.channel = null;
    this.encoding = charset.name();

    reset();
  }

  public String read() throws IOException {
    return in.readLine();
  }

  public void reset() throws IOException {

    if (inputStreamFactory != null) {
      in = new BufferedReader(new InputStreamReader(inputStreamFactory.createInputStream(), encoding));
    }
    else if (channel == null) {
        in.reset();
    }
    else {
      channel.position(0);
      in = new BufferedReader(Channels.newReader(channel, encoding));
    }
  }

  public void close() throws IOException {

      if (in != null && channel == null) {
        in.close();
      }
      else if (channel != null) {
       channel.close();
      }
  }
}
