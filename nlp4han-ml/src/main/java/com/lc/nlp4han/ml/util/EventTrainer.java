package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.util.Map;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;


public interface EventTrainer {

  public static final String EVENT_VALUE = "Event";

  public void init(Map<String, String> trainParams, Map<String, String> reportMap);
  public ClassificationModel train(ObjectStream<Event> events) throws IOException;

}
