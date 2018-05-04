package com.lc.nlp4han.pos.word;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.AbstractStringContextGenerator;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventModelSequenceTrainer;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.ml.util.SequenceTrainer;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.pos.POSTaggerProb;

/**
 * 基于词的最大熵中文词性标注器
 * 
 * 该词性标注器的输入为分词后的句子
 * 
 * @author 刘小峰
 * 
 */
public class POSTaggerWordME implements POSTaggerProb
{
    public final static int DEFAULT_BEAM_SIZE = 3;

    protected AbstractStringContextGenerator contextGen;

    protected int size;

    private Sequence bestSequence;

    private SequenceClassificationModel<String> model;

    private SequenceValidator<String> sequenceValidator;
    
    public POSTaggerWordME(File model) throws IOException
    {
        this(new ModelWrapper(model), new DefaultWordPOSContextGenerator());
    }

    public POSTaggerWordME(File model, AbstractStringContextGenerator contextGen) throws IOException
    {
        this(new ModelWrapper(model), contextGen);
    }
    
    public POSTaggerWordME(ModelWrapper model)
    {
        init(model, new DefaultWordPOSContextGenerator());

    }

    public POSTaggerWordME(ModelWrapper model, AbstractStringContextGenerator contextGen)
    {
        init(model, contextGen);

    }
    
    private void init(ModelWrapper model, AbstractStringContextGenerator contextGenerator)
    {
        int beamSize = DEFAULT_BEAM_SIZE;
        
        beamSize = model.getBeamSize();

        contextGen = contextGenerator;
        size = beamSize;

        sequenceValidator = new DefaultWordPOSSequenceValidator();

        this.model = model.getSequenceModel();
    }

    @Override
    public String[] tag(String[] sentence)
    {
        bestSequence = model.bestSequence(sentence, null, contextGen, sequenceValidator);
        List<String> t = bestSequence.getOutcomes();
        return t.toArray(new String[t.size()]);
    }

    @Override
    public String[][] tag(String[] sentence, int k)
    {
        Sequence[] bestSequences = model.bestSequences(k, sentence, null, contextGen, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        for (int si = 0; si < tags.length; si++)
        {
            List<String> t = bestSequences[si].getOutcomes();
            tags[si] = t.toArray(new String[t.size()]);
        }

        return tags;
    }

    /**
     * 训练最大熵基于词的词性标注模型
     * 
     * @param sampleStream
     *            训练语料
     * @param params
     *            最大熵模型训练参数
     * 
     * @throws java.io.IOException
     */
    public static ModelWrapper train(ObjectStream<WordPOSSample> samples, 
            TrainingParameters trainParams,
            AbstractStringContextGenerator contextGenerator) throws IOException
    {
        String beamSizeString = trainParams.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

        int beamSize = DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
          beamSize = Integer.parseInt(beamSizeString);
        };

        TrainerType trainerType = TrainerFactory.getTrainerType(trainParams.getSettings());

        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        
        ClassificationModel posModel = null;
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
          ObjectStream<Event> es = new WordPOSSampleEventStream(samples, contextGenerator);

          EventTrainer trainer = TrainerFactory.getEventTrainer(trainParams.getSettings(),
              manifestInfoEntries);
          posModel = trainer.train(es);
        }
        else if (TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType)) {
          WordPOSSampleSequenceStream ss = new WordPOSSampleSequenceStream(samples, contextGenerator);
          EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(trainParams.getSettings(),
              manifestInfoEntries);
          posModel = trainer.train(ss);
        }
        else if (TrainerType.SEQUENCE_TRAINER.equals(trainerType)) {
          SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(
              trainParams.getSettings(), manifestInfoEntries);

          // TODO: This will probably cause issue, since the feature generator uses the outcomes array

          WordPOSSampleSequenceStream ss = new WordPOSSampleSequenceStream(samples, contextGenerator);
          seqPosModel = trainer.train(ss);
        }
        else {
          throw new IllegalArgumentException("Trainer type is not supported: " + trainerType);
        }

        if (posModel != null) {
          return new ModelWrapper(posModel, beamSize);
        }
        else {
          return new ModelWrapper(seqPosModel);
        }
    }

}
