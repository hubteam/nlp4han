package com.lc.nlp4han.ml.util;

/**
 * Calculates the arithmetic mean of values
 * added with the {@link #add(double)} method.
 */
public class Mean {

  /**
   * The sum of all added values.
   */
  private double sum;

  /**
   * The number of times a value was added.
   */
  private long count;

  /**
   * Adds a value to the arithmetic mean.
   *
   * @param value the value which should be added
   * to the arithmetic mean.
   */
  public void add(double value) {
    add(value, 1);
  }

  /**
   * Adds a value count times to the arithmetic mean.
   *
   * @param value the value which should be added
   * to the arithmetic mean.
   *
   * @param count number of times the value should be added to
   * arithmetic mean.
   */
  public void add(double value, long count) {
    sum += value * count;
    this.count += count;
  }

  /**
   * Retrieves the mean of all values added with
   * {@link #add(double)} or 0 if there are zero added
   * values.
   */
  public double mean() {
    return count > 0 ? sum / count : 0;
  }

  /**
   * Retrieves the number of times a value
   * was added to the mean.
   */
  public long count() {
    return count;
  }

  @Override
  public String toString() {
    return Double.toString(mean());
  }
}
