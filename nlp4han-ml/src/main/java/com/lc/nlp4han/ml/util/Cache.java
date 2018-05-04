package com.lc.nlp4han.ml.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides fixed size, pre-allocated, least recently used replacement cache.
 */
public class Cache<K,V> extends LinkedHashMap<K,V> {

  private int capacity;

  public Cache(final int capacity) {
    this.capacity = capacity;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
    return this.size() > this.capacity;
  }
}
