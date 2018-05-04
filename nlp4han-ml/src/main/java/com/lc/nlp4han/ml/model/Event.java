package com.lc.nlp4han.ml.model;


/**
 * The context of a decision point during training.  This includes
 * contextual predicates and an outcome.
 */
public class Event {
    private String outcome;
    private String[] context;
    private float[] values;

    public Event(String outcome, String[] context) {
      this(outcome,context,null);
    }

    public Event(String outcome, String[] context, float[] values) {
      this.outcome = outcome;
      this.context = context;
      this.values = values;
    }

    public String getOutcome() {
      return outcome;
    }

    public String[] getContext() {
      return context;
    }

    public float[] getValues() {
      return values;
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(outcome).append(" [");
      if (context.length > 0) {
        sb.append(context[0]);
        if (values != null) {
          sb.append("=").append(values[0]);
        }
      }
      for (int ci=1;ci<context.length;ci++) {
        sb.append(" ").append(context[ci]);
        if (values != null) {
          sb.append("=").append(values[ci]);
        }
      }
      sb.append("]");
      return sb.toString();
    }

}
