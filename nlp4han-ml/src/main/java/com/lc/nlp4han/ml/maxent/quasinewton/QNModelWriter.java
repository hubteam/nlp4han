package com.lc.nlp4han.ml.maxent.quasinewton;

import java.io.IOException;
import java.util.List;

import com.lc.nlp4han.ml.maxent.gis.GISModelWriter;
import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.ComparablePredicate;


public abstract class QNModelWriter extends GISModelWriter {

  public QNModelWriter(AbstractModel model) {
    super(model);
  }

  @Override
  public void persist() throws IOException {
    // the type of model (QN)
    writeUTF("QN");

    // the mapping from outcomes to their integer indexes
    writeInt(OUTCOME_LABELS.length);

    for (int i = 0; i < OUTCOME_LABELS.length; i++)
      writeUTF(OUTCOME_LABELS[i]);

    // the mapping from predicates to the outcomes they contributed to.
    // The sorting is done so that we actually can write this out more
    // compactly than as the entire list.
    ComparablePredicate[] sorted = sortValues();
    List<List<ComparablePredicate>> compressed = compressOutcomes(sorted);

    writeInt(compressed.size());

    for (int i = 0; i < compressed.size(); i++) {
      List<ComparablePredicate> a = compressed.get(i);
      writeUTF(a.size() + a.get(0).toString());
    }

    // the mapping from predicate names to their integer indexes
    writeInt(PARAMS.length);

    for (int i = 0; i < sorted.length; i++)
      writeUTF(sorted[i].name);

    // write out the parameters
    for (int i = 0; i < sorted.length; i++)
      for (int j = 0; j < sorted[i].params.length; j++)
        writeDouble(sorted[i].params[j]);

    close();
  }
}

