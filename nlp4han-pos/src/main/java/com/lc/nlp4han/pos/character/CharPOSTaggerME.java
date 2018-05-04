package com.lc.nlp4han.pos.character;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.pos.POSTaggerProb;

/**
 * 基于字的最大熵中文词性标注器
 * 
 * 输入为切分后的词序列
 * 
 * @author 王馨苇
 * @author 刘小峰
 * 
 */
public class CharPOSTaggerME implements POSTaggerProb
{

    public static final int DEFAULT_BEAM_SIZE = 10;
    private CharPOSContextGenerator contextGenerator;
    private int size;
    private Sequence bestSequence;
    private SequenceClassificationModel<String> model;

    private List<String> characters = new ArrayList<>();
	private List<String> segwords = new ArrayList<>();

    private SequenceValidator<String> sequenceValidator;
    
    public CharPOSTaggerME(File model) throws IOException
    {
        this(new ModelWrapper(model), new CharPOSContextGeneratorConf());
    }

    public CharPOSTaggerME(File model, CharPOSContextGenerator contextGen) throws IOException
    {
        this(new ModelWrapper(model), contextGen);
    }
    
    public CharPOSTaggerME(ModelWrapper model) throws IOException
    {
        init(model, new CharPOSContextGeneratorConf());

    }


    /**
     * 构造函数，初始化工作
     * 
     * @param model
     *            模型
     * @param contextGen
     *            特征
     */
    public CharPOSTaggerME(ModelWrapper model, CharPOSContextGenerator contextGen)
    {
        init(model, contextGen);
    }

    /**
     * 初始化工作
     * 
     * @param model
     *            模型
     * @param contextGen
     *            特征
     */
    private void init(ModelWrapper model, CharPOSContextGenerator contextGen)
    {
        int beamSize = CharPOSTaggerME.DEFAULT_BEAM_SIZE;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultCharPOSSequenceValidator();

        this.model = model.getSequenceModel();

    }


    /**
     * 训练模型
     * 
     * @param languageCode
     *            编码
     * @param sampleStream
     *            文件流
     * @param contextGen
     *            特征
     * @param encoding
     *            编码
     * @return 模型和模型信息的包裹结果
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static ModelWrapper train(ObjectStream<CharPOSSample> sampleStream, TrainingParameters params, CharPOSContextGenerator contextGen) throws IOException
    {
        String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
        int beamSize = CharPOSTaggerME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }
        ClassificationModel posModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        // event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
        {
            // sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new CharPOSSampleEventStream(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
            posModel = trainer.train(es);
        }

        if (posModel != null) {
            return new ModelWrapper(posModel, beamSize);
          }
          else {
            return new ModelWrapper(seqPosModel);
          }
    }

    /**
     * 根据分词的数组生成字符序列，字符的标记序列
     * 
     * @param words
     *            分词之后的词语数组
     */
    private void taglabel(String[] words)
    {
    	characters.clear();
		segwords.clear();
		for (int i = 0; i < words.length; i++) {
			String temp = words[i];
			segwords.add(temp);
			if(temp.length() == 1){
				characters.add(temp+"_S");				
				continue;
			}
			for (int j = 0; j < temp.length(); j++) {
				char c = temp.charAt(j);
				if (j == 0) {
					characters.add(c + "_B");
                } else if (j == temp.length() - 1) {
                	characters.add(c + "_E");
                } else {
                	characters.add(c + "_M");
                }
			}
		}
        }

    /**
     * 得到的最好的标记序列
     * 
     * @param words
     *            分词的数组
     * @return
     */
    public String[] tag(String[] words)
    {
        taglabel(words);
        String[] characterandpos = this.tag(characters.toArray(new String[characters.size()]),
				segwords.toArray(new String[segwords.size()]));
        return CharPOSSample.toPos(characterandpos);
    }
    
    public String[] tag(String[] characters, String[] words){
		bestSequence = model.bestSequence(characters, (String[])words, contextGenerator,sequenceValidator);
//        System.out.println(Arrays.toString(words));
		List<String> t = bestSequence.getOutcomes();
        
        return t.toArray(new String[t.size()]);
	}

    /**
     * 得到最好的numTaggings个标记序列
     * 
     * @param k
     *            个数
     * @param words
     *            分词的数组
     * @return 词性标注的序列
     */
    public String[][] tag(String[] words, int k)
    {
        taglabel(words);

        Sequence[] bestSequences = model.bestSequences(k, characters.toArray(new String[characters.size()]),segwords.toArray(new String[segwords.size()]),
        		contextGenerator, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        String[][] poses = new String[bestSequences.length][];
        for (int si = 0; si < tags.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tags[si] = t.toArray(new String[t.size()]);
            poses[si] = CharPOSSample.toPos(tags[si]);
        }
        return poses;
    }

    /**
     * 最好的K个序列
     * 
     * @param sentence
     *            分词之后的词语数组
     * @return
     */
    public Sequence[] topKSequences(String[] sentence)
    {
        return this.topKSequences(sentence, null);
    }

    /**
     * 最好的K个序列
     * 
     * @param words
     *            分词之后的词语数组
     * @param additionaContext
     * @return
     */
    public Sequence[] topKSequences(String[] words, Object[] additionaContext)
    {
        taglabel(words);

        return model.bestSequences(size, characters.toArray(new String[characters.size()]),
        		segwords.toArray(new String[segwords.size()]),
        		contextGenerator, sequenceValidator);
    }

    /**
     * 生成词典
     * 
     * @param sample
     *            样本流
     * @return
     * @throws IOException
     */
    public static HashSet<String> buildDictionary(ObjectStream<CharPOSSample> samples) throws IOException
    {
        HashSet<String> dict = new HashSet<>();
        CharPOSSample sample;
        while ((sample = samples.read()) != null)
        {
            String[] words = sample.getWords();
            for (int i = 0; i < words.length; i++)
            {
                dict.add(words[i]);
            }
        }
        return dict;
    }

    /**
     * 
     * @param file
     *            训练文件
     * @param encoding
     *            编码方式
     * @return
     * @throws IOException
     */
    public static HashSet<String> buildDictionary(File file, String encoding) throws IOException
    {
        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        String sentences;
        HashSet<String> dict = new HashSet<String>();
        while ((sentences = data.readLine()) != null)
        {
            if (sentences.compareTo("") != 0)
            {
                String wordsandposes[] = sentences.split("\\s+");
                for (int i = 1; i < wordsandposes.length; i++)
                {
                    String[] wordanspos = wordsandposes[i].split("/");
                    String word = wordanspos[0];
                    dict.add(word);
                }
            }
        }

        data.close();

        return dict;
    }
}
