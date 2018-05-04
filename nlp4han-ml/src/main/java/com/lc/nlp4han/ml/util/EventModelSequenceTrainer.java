package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.util.Map;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.SequenceStream;


public interface EventModelSequenceTrainer {

  public static final String SEQUENCE_VALUE = "EventModelSequence";

  public void init(Map<String, String> trainParams, Map<String, String> reportMap);

  public ClassificationModel train(SequenceStream events) throws IOException;

}
