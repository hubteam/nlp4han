package com.lc.nlp4han.ml.model;

/**
 * Class which models a sequence.
 * @param <T> The type of the object which is the source of this sequence.
 */
public class Sequence<T> {

  private Event[] events;
  private T source;

  /**
   * Creates a new sequence made up of the specified events and derived from the
   * specified source.
   *
   * @param events
   *          The events of the sequence.
   * @param source
   *          The source object for this sequence.
   */
  public Sequence(Event[] events, T source) {
    this.events = events;
    this.source = source;
  }

  /**
   * Returns the events which make up this sequence.
   *
   * @return the events which make up this sequence.
   */
  public Event[] getEvents() {
    return events;
  }

  /**
   * Returns an object from which this sequence can be derived. This object is
   * used when the events for this sequence need to be re-derived such as in a
   * call to SequenceStream.updateContext.
   *
   * @return an object from which this sequence can be derived.
   */
  public T getSource() {
    return source;
  }
}
