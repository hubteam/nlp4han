package com.lc.nlp4han.ml.util;

import java.io.IOException;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.DataIndexer;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.HashSumEventStream;
import com.lc.nlp4han.ml.model.OnePassDataIndexer;
import com.lc.nlp4han.ml.model.TwoPassDataIndexer;


public abstract class AbstractEventTrainer extends AbstractTrainer implements
    EventTrainer {

  public static final String DATA_INDEXER_PARAM = "DataIndexer";
  public static final String DATA_INDEXER_ONE_PASS_VALUE = "OnePass";
  public static final String DATA_INDEXER_TWO_PASS_VALUE = "TwoPass";

  public AbstractEventTrainer() {
  }

  @Override
  public boolean isValid() {
    if (!super.isValid()) {
      return false;
    }

    String dataIndexer = getStringParam(DATA_INDEXER_PARAM,
        DATA_INDEXER_TWO_PASS_VALUE);

    if (dataIndexer != null) {
      if (!(DATA_INDEXER_ONE_PASS_VALUE.equals(dataIndexer) || DATA_INDEXER_TWO_PASS_VALUE
          .equals(dataIndexer))) {
        return false;
      }
    }
    // TODO: Check data indexing ...

    return true;
  }

  public abstract boolean isSortAndMerge();

  public DataIndexer getDataIndexer(ObjectStream<Event> events) throws IOException {

    String dataIndexerName = getStringParam(DATA_INDEXER_PARAM,
        DATA_INDEXER_TWO_PASS_VALUE);

    int cutoff = getCutoff();
    boolean sortAndMerge = isSortAndMerge();
    DataIndexer indexer;

    if (DATA_INDEXER_ONE_PASS_VALUE.equals(dataIndexerName)) {
      indexer = new OnePassDataIndexer(events, cutoff, sortAndMerge);
    } else if (DATA_INDEXER_TWO_PASS_VALUE.equals(dataIndexerName)) {
      indexer = new TwoPassDataIndexer(events, cutoff, sortAndMerge);
    } else {
      throw new IllegalStateException("Unexpected data indexer name: "
          + dataIndexerName);
    }
    return indexer;
  }

  public abstract ClassificationModel doTrain(DataIndexer indexer) throws IOException;

  public final ClassificationModel train(ObjectStream<Event> events) throws IOException {

    if (!isValid()) {
      throw new IllegalArgumentException("trainParams are not valid!");
    }

    HashSumEventStream hses = new HashSumEventStream(events);
    DataIndexer indexer = getDataIndexer(hses);

    ClassificationModel model = doTrain(indexer);

    addToReport("Training-Eventhash", hses.calculateHashSum().toString(16));
    addToReport(AbstractTrainer.TRAINER_TYPE_PARAM, EventTrainer.EVENT_VALUE);
    return model;
  }
}
