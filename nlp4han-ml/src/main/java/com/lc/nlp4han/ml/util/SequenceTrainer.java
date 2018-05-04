package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.util.Map;

import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.model.SequenceStream;

public interface SequenceTrainer {

  public static final String SEQUENCE_VALUE = "Sequence";

  public void init(Map<String, String> trainParams, Map<String, String> reportMap);

  public SequenceClassificationModel<String> train(SequenceStream events) throws IOException;
}
