package com.lc.nlp4han.ml.util;

import java.io.IOException;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.SequenceStream;


public abstract class AbstractEventModelSequenceTrainer extends AbstractTrainer implements
    EventModelSequenceTrainer {

  public AbstractEventModelSequenceTrainer() {
  }

  public abstract ClassificationModel doTrain(SequenceStream events)
      throws IOException;

  public final ClassificationModel train(SequenceStream events) throws IOException {

    if (!isValid()) {
      throw new IllegalArgumentException("trainParams are not valid!");
    }

    ClassificationModel model = doTrain(events);
    addToReport(AbstractTrainer.TRAINER_TYPE_PARAM,
        EventModelSequenceTrainer.SEQUENCE_VALUE);
    return model;
  }

}
