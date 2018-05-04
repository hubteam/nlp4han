package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import com.lc.nlp4han.ml.model.Event;

public abstract class AbstractEventStream<T> implements ObjectStream<Event> {

  private ObjectStream<T> samples;

  private Iterator<Event> events = Collections.<Event>emptyList().iterator();

  /**
   * Initializes the current instance with a sample {@link Iterator}.
   *
   * @param samples the sample {@link Iterator}.
   */
  public AbstractEventStream(ObjectStream<T> samples) {
    this.samples = samples;
  }

  /**
   * Creates events for the provided sample.
   *
   * @param sample the sample for which training {@link Event}s
   * are be created.
   *
   * @return an {@link Iterator} of training events or
   * an empty {@link Iterator}.
   */
  protected abstract Iterator<Event> createEvents(T sample);

  @Override
  public final Event read() throws IOException {

    if (events.hasNext()) {
      return events.next();
    }
    else {
      T sample;
      while (!events.hasNext() && (sample = samples.read()) != null) {
        events = createEvents(sample);
      }

      if (events.hasNext()) {
        return read();
      }
    }

    return null;
  }

  @Override
  public void reset() throws IOException, UnsupportedOperationException {
    events = Collections.emptyIterator();
    samples.reset();
  }

  @Override
  public void close() throws IOException {
    samples.close();
  }
}
