package com.lc.nlp4han.ml.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.ml.util.InsufficientTrainingDataException;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * An indexer for maxent model data which handles cutoffs for uncommon
 * contextual predicates and provides a unique integer index for each of the
 * predicates and maintains event values.
 */
public class OnePassRealValueDataIndexer extends OnePassDataIndexer {

  float[][] values;

  public OnePassRealValueDataIndexer(ObjectStream<Event> eventStream, int cutoff, boolean sort) throws IOException {
    super(eventStream,cutoff,sort);
  }

  /**
   * Two argument constructor for DataIndexer.
   * @param eventStream An Event[] which contains the a list of all the Events
   *               seen in the training data.
   * @param cutoff The minimum number of times a predicate must have been
   *               observed in order to be included in the model.
   */
  public OnePassRealValueDataIndexer(ObjectStream<Event> eventStream, int cutoff) throws IOException {
    super(eventStream,cutoff);
  }

  public float[][] getValues() {
    return values;
  }

  protected int sortAndMerge(List<ComparableEvent> eventsToCompare,boolean sort) throws InsufficientTrainingDataException {
    int numUniqueEvents = super.sortAndMerge(eventsToCompare,sort);
    values = new float[numUniqueEvents][];
    int numEvents = eventsToCompare.size();
    for (int i = 0, j = 0; i < numEvents; i++) {
      ComparableEvent evt = eventsToCompare.get(i);
      if (null == evt) {
        continue; // this was a dupe, skip over it.
      }
      values[j++] = evt.values;
    }
    return numUniqueEvents;
  }

  protected List<ComparableEvent> index(LinkedList<Event> events, Map<String,Integer> predicateIndex) {
    Map<String,Integer> omap = new HashMap<>();

    int numEvents = events.size();
    int outcomeCount = 0;
    List<ComparableEvent> eventsToCompare = new ArrayList<>(numEvents);
    List<Integer> indexedContext = new ArrayList<>();

    for (int eventIndex=0; eventIndex<numEvents; eventIndex++) {
      Event ev = events.removeFirst();
      String[] econtext = ev.getContext();
      ComparableEvent ce;

      int ocID;
      String oc = ev.getOutcome();

      if (omap.containsKey(oc)) {
        ocID = omap.get(oc);
      } else {
        ocID = outcomeCount++;
        omap.put(oc, ocID);
      }

      for (String pred : econtext) {
        if (predicateIndex.containsKey(pred)) {
          indexedContext.add(predicateIndex.get(pred));
        }
      }

      //drop events with no active features
      if (indexedContext.size() > 0) {
        int[] cons = new int[indexedContext.size()];
        for (int ci=0;ci<cons.length;ci++) {
          cons[ci] = indexedContext.get(ci);
        }
        ce = new ComparableEvent(ocID, cons, ev.getValues());
        eventsToCompare.add(ce);
      }
      else {
        System.err.println("Dropped event "+ev.getOutcome()+":"+Arrays.asList(ev.getContext()));
      }
//    recycle the TIntArrayList
      indexedContext.clear();
    }
    outcomeLabels = toIndexedStringArray(omap);
    predLabels = toIndexedStringArray(predicateIndex);
    return eventsToCompare;
  }

}
