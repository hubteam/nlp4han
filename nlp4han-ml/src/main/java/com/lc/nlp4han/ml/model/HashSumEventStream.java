package com.lc.nlp4han.ml.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lc.nlp4han.ml.util.AbstractObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;


public class HashSumEventStream extends AbstractObjectStream<Event> {

  private MessageDigest digest;

  public HashSumEventStream(ObjectStream<Event> eventStream) {
    super(eventStream);

    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      // should never happen, does all java runtimes have md5 ?!
     throw new IllegalStateException(e);
    }
  }

  @Override
  public Event read() throws IOException {
    Event event = super.read();

    if (event != null) {
      try {
        digest.update(event.toString().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e) {
        throw new IllegalStateException("UTF-8 encoding is not available!", e);
      }
    }

    return event;
  }

  /**
   * Calculates the hash sum of the stream. The method must be
   * called after the stream is completely consumed.
   *
   * @return the hash sum
   * @throws IllegalStateException if the stream is not consumed completely,
   * completely means that hasNext() returns false
   */
  public BigInteger calculateHashSum() {
    return new BigInteger(1, digest.digest());
  }

  public void remove() {
  }
}
