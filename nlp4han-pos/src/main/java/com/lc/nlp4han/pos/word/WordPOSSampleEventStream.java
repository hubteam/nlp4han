package com.lc.nlp4han.pos.word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.AbstractStringContextGenerator;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * This class reads the {@link WordPOSSample}s from the given {@link Iterator}
 * and converts the {@link WordPOSSample}s into {@link Event}s which
 * can be used by the maxent library for training.
 */
public class WordPOSSampleEventStream extends AbstractEventStream<WordPOSSample> {

  /**
   * The {@link POSContextGenerator} used
   * to create the training {@link Event}s.
   */
  private AbstractStringContextGenerator cg;

  /**
   * Initializes the current instance with the given samples and the
   * given {@link POSContextGenerator}.
   *
   * @param samples
   * @param cg
   */
  public WordPOSSampleEventStream(ObjectStream<WordPOSSample> samples, AbstractStringContextGenerator cg) {
    super(samples);

    this.cg = cg;
  }

  /**
   * Initializes the current instance with given samples
   * and a {@link DefaultPOSContextGenerator}.
   * @param samples
   */
  public WordPOSSampleEventStream(ObjectStream<WordPOSSample> samples) {
    this(samples, new DefaultWordPOSContextGenerator());
  }

  @Override
  protected Iterator<Event> createEvents(WordPOSSample sample) {
    String sentence[] = sample.getSentence();
    String tags[] = sample.getTags();
    Object ac[] = sample.getAddictionalContext();
    List<Event> events = generateEvents(sentence, tags, ac, cg);
    return events.iterator();
  }

  public static List<Event> generateEvents(String[] sentence, String[] tags,
      Object[] additionalContext, AbstractStringContextGenerator cg) {
    List<Event> events = new ArrayList<Event>(sentence.length);

    for (int i=0; i < sentence.length; i++) {

      // it is safe to pass the tags as previous tags because
      // the context generator does not look for non predicted tags
      String[] context = cg.getContext(i, sentence, tags, additionalContext);

      events.add(new Event(tags[i], context));
    }
    return events;
  }

  public static List<Event> generateEvents(String[] sentence, String[] tags,
          AbstractStringContextGenerator cg) {
    return generateEvents(sentence, tags, null, cg);
  }
}
