package com.lc.nlp4han.ner.character;

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
import com.lc.nlp4han.ner.word.NERWordOrCharacterSample;

/**
 * 为基于字的命名实体识别训练模型
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class NERCharacterME implements NERCharacter
{

	public static final int DEFAULT_BEAM_SIZE = 3;
	private NERCharacterContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
	private SequenceValidator<String> sequenceValidator;

	public NERCharacterME(File model) throws IOException
	{
		this(new ModelWrapper(model), new NERCharacterContextGeneratorConf());
	}

	public NERCharacterME(File model, NERCharacterContextGenerator contextGen) throws IOException
	{
		this(new ModelWrapper(model), contextGen);
	}

	public NERCharacterME(ModelWrapper model, NERCharacterContextGenerator contextGen)
	{
		init(model, contextGen);

	}

	public NERCharacterME(ModelWrapper model) throws IOException
	{
		init(model, new NERCharacterContextGeneratorConf());

	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */
	private void init(ModelWrapper model, NERCharacterContextGenerator contextGen)
	{
		int beamSize = NERCharacterME.DEFAULT_BEAM_SIZE;

		contextGenerator = contextGen;
		size = beamSize;
		sequenceValidator = new DefaultNERCharacterSequenceValidator();

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
	public static ModelWrapper train(ObjectStream<NERWordOrCharacterSample> sampleStream, TrainingParameters params,
			NERCharacterContextGenerator contextGen) throws IOException
	{
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = NERCharacterME.DEFAULT_BEAM_SIZE;
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
			ObjectStream<Event> es = new NERCharacterSampleEvent(sampleStream, contextGen);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			posModel = trainer.train(es);
		}

		if (posModel != null)
		{
			return new ModelWrapper(posModel, beamSize);
		}
		else
		{
			return new ModelWrapper(seqPosModel);
		}
	}

	public String[] tag(String[] characters, Object[] additionaContext)
	{
		bestSequence = model.bestSequence(characters, additionaContext, contextGenerator, sequenceValidator);
		List<String> t = bestSequence.getOutcomes();
		return t.toArray(new String[t.size()]);
	}

	/**
	 * 生语料转换成字符串数组
	 * 
	 * @param sentence
	 *            读入的生语料
	 * @return
	 */
	private String[] tocharacters(String sentence)
	{
		String[] chars = new String[sentence.length()];
		for (int i = 0; i < sentence.length(); i++)
		{
			chars[i] = sentence.charAt(i) + "";
		}
		return chars;
	}

	/**
	 * 得到最好的numTaggings个标记序列
	 * 
	 * @param numTaggings
	 *            个数
	 * @param characters
	 *            一个个字
	 * @return 分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] characters)
	{
		Sequence[] bestSequences = model.bestSequences(numTaggings, characters, null, contextGenerator,
				sequenceValidator);
		String[][] tagsandposes = new String[bestSequences.length][];
		for (int si = 0; si < tagsandposes.length; si++)
		{
			List<String> t = bestSequences[si].getOutcomes();
			tagsandposes[si] = t.toArray(new String[t.size()]);

		}
		return tagsandposes;
	}

	/**
	 * 最好的K个序列
	 * 
	 * @param characters
	 *            一个个字
	 * @return
	 */
	public Sequence[] topKSequences(String[] characters)
	{
		return this.topKSequences(characters, null);
	}

	/**
	 * 最好的K个序列
	 * 
	 * @param characters
	 *            一个个字
	 * @param additionaContext
	 * @return
	 */
	public Sequence[] topKSequences(String[] characters, Object[] additionaContext)
	{
		return model.bestSequences(size, characters, additionaContext, contextGenerator, sequenceValidator);
	}

	/**
	 * 返回一个ner实体
	 * 
	 * @param begin
	 *            开始位置
	 * @param tags
	 *            标记序列
	 * @param words
	 *            词语序列
	 * @param flag
	 *            实体标记
	 * @return
	 */
	public NamedEntity getNer(int begin, String[] tags, String[] words, String flag)
	{
		NamedEntity ner = new NamedEntity();
		for (int i = begin; i < tags.length; i++)
		{
			List<String> wordStr = new ArrayList<>();
			String word = "";
			if (tags[i].equals(flag))
			{
				ner.setStart(i);
				word += words[i];
				wordStr.add(words[i]);
				for (int j = i + 1; j < tags.length; j++)
				{
					if (tags[j].equals(flag))
					{
						word += words[j];
						wordStr.add(words[j]);
						if (j == tags.length - 1)
						{
							ner.setString(word);
							ner.setType(flag);
							ner.setWords(wordStr.toArray(new String[wordStr.size()]));
							ner.setEnd(j);
							break;
						}
					}
					else
					{
						ner.setString(word);
						ner.setType(flag);
						ner.setWords(wordStr.toArray(new String[wordStr.size()]));
						ner.setEnd(j - 1);
						break;
					}
				}
			}
			else if (tags[i].split("_")[1].equals(flag) && tags[i].split("_")[0].equals("b"))
			{
				ner.setStart(i);
				word += words[i];
				wordStr.add(words[i]);
				for (int j = i + 1; j < tags.length; j++)
				{
					word += words[j];
					wordStr.add(words[j]);
					if (tags[j].split("_")[1].equals(flag) && tags[j].split("_")[0].equals("m"))
					{

					}
					else if (tags[j].split("_")[1].equals(flag) && tags[j].split("_")[0].equals("e"))
					{
						ner.setString(word);
						ner.setType(flag);
						ner.setWords(wordStr.toArray(new String[wordStr.size()]));
						ner.setEnd(j);
						break;
					}
				}
			}
			else
			{
				if (tags[i].split("_")[1].equals(flag) && tags[i].split("_")[0].equals("s"))
				{
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
	 * 读入一段单个字组成的语料
	 * 
	 * @param sentence
	 *            单个字组成的数组
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String[] sentence)
	{
		String[] tags = tag(sentence, null);
		List<NamedEntity> ners = new ArrayList<>();
		for (int i = 0; i < tags.length; i++)
		{
			String flag;
			if (tags[i].equals("o"))
			{
				flag = "o";
			}
			else
			{
				flag = tags[i].split("_")[1];
			}
			if (ners.size() == 0)
			{
				ners.add(getNer(0, tags, sentence, flag));
			}
			else
			{
				ners.add(getNer(i, tags, sentence, flag));
			}
			i = ners.get(ners.size() - 1).getEnd();
		}
		return ners.toArray(new NamedEntity[ners.size()]);
	}

	/**
	 * 得到命名实体识别的结果
	 */
	@Override
	public NamedEntity[] ner(String sentence)
	{
		String[] tags = tocharacters(sentence);
		return ner(tags);
	}

	/**
	 * 读入一句生语料，进行标注，得到指定的命名实体
	 * 
	 * @param sentence
	 *            读取的生语料
	 * @param flag
	 *            命名实体标记
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String sentence, String flag)
	{
		String[] tags = tocharacters(sentence);
		return ner(tags, flag);
	}

	/**
	 * 读入一段单个字组成的语料,得到指定的命名实体
	 * 
	 * @param sentence
	 *            单个字组成的数组
	 * @param flag
	 *            命名实体标记
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String[] sentence, String flag)
	{
		NamedEntity[] ners = ner(sentence);
		for (int i = 0; i < ners.length; i++)
		{
			if (ners[i].getType().equals(flag))
			{

			}
			else
			{
				ners[i].setType("o");
			}
		}
		return ners;
	}

	/**
	 * 读入一句生语料，进行标注，得到最好的K个结果
	 * 
	 * @param sentence
	 *            读取的生语料
	 * @return
	 */
	public NamedEntity[][] ner(String sentence, int k)
	{
		String[] tags = tocharacters(sentence);
		return ner(tags, k);
	}

	/**
	 * 读入一段单个字组成的语料,得到最好的K个结果
	 * 
	 * @param sentence
	 *            单个字组成的数组
	 * @return
	 */
	public NamedEntity[][] ner(String[] sentence, int k)
	{
		String[][] tags = tag(k, sentence);
		NamedEntity[][] kners = new NamedEntity[k][];
		for (int i = 0; i < tags.length; i++)
		{
			List<NamedEntity> ners = new ArrayList<>();
			for (int j = 0; j < tags[i].length; j++)
			{
				String flag;
				if (tags[j].equals("o"))
				{
					flag = "o";
				}
				else
				{
					flag = tags[i][j].split("_")[1];
				}
				if (ners.size() == 0)
				{
					ners.add(getNer(0, tags[i], sentence, flag));
				}
				else
				{
					ners.add(getNer(j, tags[i], sentence, flag));
				}
				j = ners.get(ners.size() - 1).getEnd();
			}
			kners[i] = ners.toArray(new NamedEntity[ners.size()]);
		}
		return kners;
	}
}
