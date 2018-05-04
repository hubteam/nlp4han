package com.lc.nlp4han.ml.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * Class which turns a sequence stream into an event stream.
 */
public class SequenceStreamEventStream implements ObjectStream<Event> {

  private final SequenceStream sequenceStream;

  private Iterator<Event> eventIt = Collections.emptyListIterator();

  public SequenceStreamEventStream(SequenceStream sequenceStream) {
    this.sequenceStream = sequenceStream;
  }

  @Override
  public Event read() throws IOException {
    while (!eventIt.hasNext()) {
      Sequence<?> sequence = sequenceStream.read();
      if (sequence == null) {
        return null;
      }
      eventIt = Arrays.asList(sequence.getEvents()).iterator();
    }
    return eventIt.next();
  }

  @Override
  public void reset() throws IOException, UnsupportedOperationException {
    eventIt = Collections.emptyListIterator();
    sequenceStream.reset();
  }

  @Override
  public void close() throws IOException {
    eventIt = Collections.emptyListIterator();
    sequenceStream.close();
  }
}
