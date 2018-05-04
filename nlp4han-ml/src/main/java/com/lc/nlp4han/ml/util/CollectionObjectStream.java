package com.lc.nlp4han.ml.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionObjectStream<E> implements ObjectStream<E> {
  private Collection<E> collection;

  private Iterator<E> iterator;

  public CollectionObjectStream(Collection<E> collection) {
    this.collection = collection;

    reset();
  }

  public E read() {
    if (iterator.hasNext())
      return iterator.next();
    else
      return null;
  }

  public void reset() {
    this.iterator = collection.iterator();
  }

  public void close() {
  }
}