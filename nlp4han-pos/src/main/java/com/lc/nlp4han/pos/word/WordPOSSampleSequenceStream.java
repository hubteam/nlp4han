 package com.lc.nlp4han.pos.word;

import java.io.IOException;

import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.Sequence;
import com.lc.nlp4han.ml.model.SequenceStream;
import com.lc.nlp4han.ml.util.AbstractStringContextGenerator;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.pos.POSTagger;


public class WordPOSSampleSequenceStream implements SequenceStream {

  private AbstractStringContextGenerator pcg;
  private ObjectStream<WordPOSSample> psi;

  public WordPOSSampleSequenceStream(ObjectStream<WordPOSSample> psi) throws IOException {
    this(psi, new DefaultWordPOSContextGenerator());
  }

  public WordPOSSampleSequenceStream(ObjectStream<WordPOSSample> psi, AbstractStringContextGenerator pcg)
      throws IOException {
    this.psi = psi;
    this.pcg = pcg;
  }

  @SuppressWarnings("unchecked")
  public Event[] updateContext(@SuppressWarnings("rawtypes") Sequence sequence, AbstractModel model) {
    Sequence<WordPOSSample> pss = sequence;
    POSTagger tagger = new POSTaggerWordME(new ModelWrapper(model));
    String[] sentence = pss.getSource().getSentence();
    Object[] ac = pss.getSource().getAddictionalContext();
    String[] tags = tagger.tag(pss.getSource().getSentence());
    Event[] events = new Event[sentence.length];
    WordPOSSampleEventStream.generateEvents(sentence, tags, ac, pcg)
        .toArray(events);
    return events;
  }

  @SuppressWarnings("rawtypes")
@Override
  public Sequence read() throws IOException {

    WordPOSSample sample = psi.read();

    if (sample != null) {
      String sentence[] = sample.getSentence();
      String tags[] = sample.getTags();
      Event[] events = new Event[sentence.length];

      for (int i=0; i < sentence.length; i++) {

        // it is safe to pass the tags as previous tags because
        // the context generator does not look for non predicted tags
        String[] context = pcg.getContext(i, sentence, tags, null);

        events[i] = new Event(tags[i], context);
      }
      Sequence<WordPOSSample> sequence = new Sequence<WordPOSSample>(events,sample);
      return sequence;
    }

    return null;
  }

  @Override
  public void reset() throws IOException, UnsupportedOperationException {
    psi.reset();
  }

  @Override
  public void close() throws IOException {
    psi.close();
  }
}

