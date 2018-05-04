package com.lc.nlp4han.ner.word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.lc.nlp4han.ner.NamedEntity;


/**
 * 为基于分词的命名实体识别训练模型
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class NERWordME implements NERWord{

	public static final int DEFAULT_BEAM_SIZE = 3;
	private NERWordContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;

    private SequenceValidator<String> sequenceValidator;
	
    public NERWordME(File model) throws IOException
    {
        this(new ModelWrapper(model), new NERWordContextGeneratorConf());
    }

    public NERWordME(File model, NERWordContextGenerator contextGen) throws IOException
    {
        this(new ModelWrapper(model), contextGen);
    }


    public NERWordME(ModelWrapper model, NERWordContextGenerator contextGen)
    {
        init(model, contextGen);

    }
    
    public NERWordME(ModelWrapper model) throws IOException
    {
        init(model, new NERWordContextGeneratorConf());

    }
    
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(ModelWrapper model, NERWordContextGenerator contextGen) {
		int beamSize = NERWordME.DEFAULT_BEAM_SIZE;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultNERWordSequenceValidator();

        this.model = model.getSequenceModel();
	}

	/**
	 * 训练模型
	 * @param languageCode 编码
	 * @param sampleStream 文件流
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static ModelWrapper train(ObjectStream<NERWordOrCharacterSample> sampleStream, TrainingParameters params,
			NERWordContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = NERWordME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        ClassificationModel posModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new NERWordSampleEvent(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
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
	 * 得到最好的一个序列
	 * @param words
	 * @param additionaContext
	 * @return
	 */
	public String[] tag(String[] words,Object[] additionaContext){
		bestSequence = model.bestSequence(words, additionaContext, contextGenerator,sequenceValidator);
      //  System.out.println(bestSequence);
		List<String> t = bestSequence.getOutcomes();
		return t.toArray(new String[t.size()]);
	}

	
	 /**
     * 最好的K个序列
     * @param words 一个个词语
     * @param additionaContext
     * @return 
     */
    public Sequence[] topKSequences(String[] words, Object[] additionaContext) {
        return model.bestSequences(size, words, additionaContext,
        		contextGenerator, sequenceValidator);
    }
	
    /**
	 * 最好的K个序列
	 * @param words 一个个词语
	 * @return
	 */
    public Sequence[] topKSequences(String[] words) {
        return this.topKSequences(words, null);
    }
    
    /**
	 * 得到最好的numTaggings个标记序列
	 * @param numTaggings 个数
	 * @param words 一个个词语
	 * @return 分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] words) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, words, null,
        		contextGenerator, sequenceValidator);
        String[][] tagsandposes = new String[bestSequences.length][];
        for (int si = 0; si < tagsandposes.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tagsandposes[si] = t.toArray(new String[t.size()]);
        }
        return tagsandposes;
    }
    
	/**
	 * 对分词之后的句子进行命名实体识别,分词之间用空格隔开
	 */
	@Override
	public NamedEntity[] ner(String sentence) {
		String[] words = sentence.split("\\s+");
		return ner(words);
	}

	/**
	 * 对分词之后的数组进行命名实体识别
	 */
	@Override
	public NamedEntity[] ner(String[] words) {
		String[] tags = tag(words, null);
		List<NamedEntity> ners = new ArrayList<>();
		for (int i = 0; i < tags.length; i++) {
			String flag;
			if(tags[i].equals("o")){
				flag = "o";
			}else{
				flag = tags[i].split("_")[1];
			}
			
			if(ners.size() == 0){
				ners.add(getNer(0, tags, words, flag));
			}else{
				ners.add(getNer(i, tags, words, flag));
			}
			
			i = ners.get(ners.size()-1).getEnd();
		}
		
 		return ners.toArray(new NamedEntity[ners.size()]);
	}
	
	 /**
	   * 返回一个ner实体
	   * @param begin 开始位置
	   * @param tags 标记序列
	   * @param words 词语序列
	   * @param flag 实体标记
	   * @return
	   */
	private NamedEntity getNer(int begin, String[] tags, String[] words, String flag){
		NamedEntity ner = new NamedEntity();
		for (int i = begin; i < tags.length; i++) {
			List<String> wordStr = new ArrayList<>();
			String word = "";
			if(tags[i].equals(flag)){
				ner.setStart(i);
				word += words[i];
				wordStr.add(words[i]);
				for (int j = i+1; j < tags.length; j++) {
					if(tags[j].equals(flag)){
						word += words[j];
						wordStr.add(words[j]);
						if(j == tags.length-1){
							ner.setString(word);
							ner.setType(flag);
							ner.setWords(wordStr.toArray(new String[wordStr.size()]));
							ner.setEnd(j);
							break;
						}
					}else{
						ner.setString(word);
						ner.setType(flag);
						ner.setWords(wordStr.toArray(new String[wordStr.size()]));
						ner.setEnd(j-1);
						break;
					}
				}
			}else if(tags[i].split("_")[1].equals(flag) && tags[i].split("_")[0].equals("b")){
				ner.setStart(i);
				word += words[i];
				wordStr.add(words[i]);
				for (int j = i+1; j < tags.length; j++) {
					word += words[j];
					wordStr.add(words[j]);
					if(tags[j].split("_")[1].equals(flag) && tags[j].split("_")[0].equals("m")){
							
					}else if(tags[j].split("_")[1].equals(flag) && tags[j].split("_")[0].equals("e")){
						ner.setString(word);
						ner.setType(flag);
						ner.setWords(wordStr.toArray(new String[wordStr.size()]));
						ner.setEnd(j);
						break;
					}
				}
			}else{
				if(tags[i].split("_")[1].equals(flag) && tags[i].split("_")[0].equals("s")){
					ner.setStart(i);
					word += words[i];
					wordStr.add(words[i]);
					ner.setString(word);
					ner.setType(flag);
					ner.setWords(wordStr.toArray(new String[wordStr.size()]));
					ner.setEnd(i);
					break;
				}
			}
			break;
		}
		return ner;
	}
	
	/**
	 * 读入一句分词的语料，得到指定的命名实体
	 * @param sentence 读取的分词的语料
	 * @param flag 命名实体标记
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String sentence, String flag) {
		String[] words = sentence.split("\\s+");
		return ner(words, flag);
	}
	
	/**
	 * 读入分词的语料，得到指定的命名实体
	 * @param words 词语
	 * @param flag 命名实体标记
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String[] words, String flag) {
		NamedEntity[] ners = ner(words);
		for (int i = 0; i < ners.length; i++) {
			if(ners[i].getType().equals(flag)){
				
			}else{
				ners[i].setType("o");
			}
		}
 		return ners;
	}
	
	/**
	 * 读入一句分词的语料，得到最好的K个结果
	 * @param sentence 读取的分词的语料
	 * @return
	 */
	public NamedEntity[][] ner(String sentence, int k){
		String[] words = sentence.split("\\s+");
		return ner(words,k);
	}
	
	/**
	 * 读入分词的语料，得到最好的K个命名实体
	 * @param words 词语
	 * @return
	 */
	public NamedEntity[][] ner(String[] words, int k){
		String[][] tags = tag(k, words);
		NamedEntity[][] kners = new NamedEntity[k][];
		for (int i = 0; i < tags.length; i++) {
			List<NamedEntity> ners = new ArrayList<>();
			for (int j = 0; j < tags[i].length; j++) {
				String flag;
				if(tags[i][j].equals("o")){
					flag = "o";
				}else{
					flag = tags[i][j].split("_")[1];
				}
				if(ners.size() == 0){
					ners.add(getNer(0,tags[i],words,flag));
				}else{
					ners.add(getNer(j,tags[i],words,flag));
				}
				j = ners.get(ners.size()-1).getEnd();
			}
			kners[i] = ners.toArray(new NamedEntity[ners.size()]);
		}
 		return kners;
	}
}
