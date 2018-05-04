package com.lc.nlp4han.ml.model;

/**
 * A maxent predicate representation which we can use to sort based on the
 * outcomes. This allows us to make the mapping of features to their parameters
 * much more compact.
 */
public class ComparablePredicate implements Comparable<ComparablePredicate> {
  public String name;
  public int[] outcomes;
  public double[] params;

  public ComparablePredicate(String n, int[] ocs, double[] ps) {
    name = n;
    outcomes = ocs;
    params = ps;
  }

  public int compareTo(ComparablePredicate cp) {
    int smallerLength = outcomes.length > cp.outcomes.length?
        cp.outcomes.length : outcomes.length;

    for (int i=0; i<smallerLength; i++) {
      if (outcomes[i] < cp.outcomes[i]) return -1;
      else if (outcomes[i] > cp.outcomes[i]) return 1;
    }

    if (outcomes.length < cp.outcomes.length) return -1;
    else if (outcomes.length > cp.outcomes.length) return 1;

    return 0;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int outcome : outcomes) {
      s.append(" ").append(outcome);
    }
    return s.toString();
  }

}

