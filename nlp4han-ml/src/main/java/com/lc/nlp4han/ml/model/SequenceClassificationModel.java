package com.lc.nlp4han.ml.model;

import com.lc.nlp4han.ml.util.BeamSearchContextGenerator;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.ml.util.SequenceValidator;

/**
 * A classification model that can label an input sequence.
 *
 * @param <T>
 */
public interface SequenceClassificationModel<T> {

  /**
   * Finds the sequence with the highest probability.
   *
   * @param sequence
   * @param additionalContext
   * @param cg
   * @param validator
   *
   * @return
   */
  Sequence bestSequence(T[] sequence, Object[] additionalContext,
      BeamSearchContextGenerator<T> cg, SequenceValidator<T> validator);

  /**
   * Finds the n most probable sequences.
   *
   * @param sequence
   * @param additionalContext
   * @param cg
   * @param validator
   *
   * @return
   */
  Sequence[] bestSequences(int numSequences, T[] sequence,
      Object[] additionalContext, double minSequenceScore, BeamSearchContextGenerator<T> cg, SequenceValidator<T> validator);

  /**
   * Finds the n most probable sequences.
   *
   * @param sequence
   * @param additionalContext
   * @param cg
   * @param validator
   *
   * @return
   */
  Sequence[] bestSequences(int numSequences, T[] sequence,
      Object[] additionalContext, BeamSearchContextGenerator<T> cg, SequenceValidator<T> validator);

  /**
   * Returns all possible outcomes.
   *
   * @return
   */
  String[] getOutcomes();
}
