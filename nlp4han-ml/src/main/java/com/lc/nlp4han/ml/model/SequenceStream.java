package com.lc.nlp4han.ml.model;

import com.lc.nlp4han.ml.util.ObjectStream;

/**
 *  Interface for streams of sequences used to train sequence models.
 */
public interface SequenceStream extends ObjectStream<Sequence> {

  /**
   * Creates a new event array based on the outcomes predicted by the specified parameters
   * for the specified sequence.
   * @param sequence The sequence to be evaluated.
   * @return event array
   */
  Event[] updateContext(Sequence sequence, AbstractModel model);

}
