package com.lc.nlp4han.ml.util;

import java.io.IOException;

import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.model.SequenceStream;

public abstract class AbstractSequenceTrainer extends AbstractTrainer implements
    SequenceTrainer {

  public AbstractSequenceTrainer() {
  }

  public abstract SequenceClassificationModel doTrain(SequenceStream events)
      throws IOException;

  public final SequenceClassificationModel train(SequenceStream events) throws IOException {

    if (!isValid()) {
      throw new IllegalArgumentException("trainParams are not valid!");
    }

    SequenceClassificationModel model = doTrain(events);
    addToReport(AbstractTrainer.TRAINER_TYPE_PARAM, SequenceTrainer.SEQUENCE_VALUE);
    return model;
  }

}
