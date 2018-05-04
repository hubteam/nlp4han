package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Reads <code>Object</code>s from a stream.
 * <p>
 * Design Decision:<br>
 * This interface provides a means for iterating over the
 * objects in a stream, it does not implement {@link java.util.Iterator} or
 * {@link Iterable} because:
 * <ul>
 * <li>{@link java.util.Iterator#next()} and
 * {@link java.util.Iterator#hasNext()} are declared as throwing no checked
 * exceptions. Thus the {@link IOException}s thrown by {@link #read()} would
 * have to be wrapped in {@link RuntimeException}s, and the compiler would be
 * unable to force users of this code to catch such exceptions.</li>
 * <li>Implementing {@link Iterable} would mean either silently calling
 * {@link #reset()} to guarantee that all items were always seen on each
 * iteration, or documenting that the Iterable only iterates over the remaining
 * elements of the ObjectStream. In either case, users not reading the
 * documentation carefully might run into unexpected behavior.</li>
 * </ul>
 *
 * @see ObjectStreamException
 */
public interface ObjectStream<T> extends AutoCloseable {

  /**
   * Returns the next object. Calling this method repeatedly until it returns
   * null will return each object from the underlying source exactly once.
   *
   * @return the next object or null to signal that the stream is exhausted
   *
   * @throws IOException if there is an error during reading
   */
  T read() throws IOException;

  /**
   * Repositions the stream at the beginning and the previously seen object sequence
   * will be repeated exactly. This method can be used to re-read
   * the stream if multiple passes over the objects are required.
   *
   * The implementation of this method is optional.
   *
   * @throws IOException if there is an error during reseting the stream
   */
  void reset() throws IOException, UnsupportedOperationException;

  /**
   * Closes the <code>ObjectStream</code> and releases all allocated
   * resources. After close was called its not allowed to call
   * read or reset.
   *
   * @throws IOException if there is an error during closing the stream
   */
  void close() throws IOException;
}
