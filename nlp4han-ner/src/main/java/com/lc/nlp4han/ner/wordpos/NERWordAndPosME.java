package com.lc.nlp4han.ner.wordpos;

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
import com.lc.nlp4han.ner.NamedEntity;
import com.lc.nlp4han.ner.word.NERWordOrCharacterSample;

/**
 * 为基于词性标注的命名实体识别训练模型，标记序列
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class NERWordAndPosME implements NERWordAndPos
{
	public static final int DEFAULT_BEAM_SIZE = 3;
	private NERWordAndPosContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;

	private SequenceValidator<String> sequenceValidator;

	public NERWordAndPosME(File model) throws IOException
	{
		this(new ModelWrapper(model), new NERWordAndPosContextGeneratorConf());
	}

	public NERWordAndPosME(File model, NERWordAndPosContextGenerator contextGen) throws IOException
	{
		this(new ModelWrapper(model), contextGen);
	}

	public NERWordAndPosME(ModelWrapper model, NERWordAndPosContextGenerator contextGen)
	{
		init(model, contextGen);

	}

	public NERWordAndPosME(ModelWrapper model) throws IOException
	{
		init(model, new NERWordAndPosContextGeneratorConf());

	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */
	private void init(ModelWrapper model, NERWordAndPosContextGenerator contextGen)
	{
		int beamSize = NERWordAndPosME.DEFAULT_BEAM_SIZE;

		contextGenerator = contextGen;
		size = beamSize;
		sequenceValidator = new DefaultNERWordAndPosSequenceValidator();

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
			NERWordAndPosContextGenerator contextGen) throws IOException
	{
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = NERWordAndPosME.DEFAULT_BEAM_SIZE;
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
			ObjectStream<Event> es = new NERWordAndPosSampleEvent(sampleStream, contextGen);
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

	public String[] tag(String[] words, String[] pos, Object[] additionaContext)
	{
		bestSequence = model.bestSequence(words, pos, contextGenerator, sequenceValidator);
		List<String> t = bestSequence.getOutcomes();
		return t.toArray(new String[t.size()]);
	}

	/**
	 * 为了人名实体的解析生成的后缀语料
	 * 
	 * @param samples
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> bulidDictionary(ObjectStream<NERWordAndPosSample> samples) throws IOException
	{
		HashSet<String> dict = new HashSet<String>();
		NERWordAndPosSample sample = null;
		while ((sample = samples.read()) != null)
		{
			String[] words = sample.getWords();
			String[] poses = sample.getPoses();

			for (int i = 1; i < words.length; i++)
			{
				if (i == 1)
				{
					if (poses[i - 1].equals("nr") && poses[i].equals("n"))
					{
						dict.add(words[i]);
					}
				}
				else
				{
					if (!(poses[i - 2].equals("nr")) && poses[i - 1].equals("nr") && poses[i].equals("n"))
					{
						dict.add(words[i]);
					}
				}
			}
		}
		return dict;
	}

	public static HashSet<String> buildDictionary(File corpusFile, String encoding) throws IOException
	{
		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(corpusFile), encoding));
		String sentence;
		HashSet<String> dict = new HashSet<String>();
		while ((sentence = data.readLine()) != null)
		{
			String words[] = sentence.split("\\s+");
			for (int i = 1; i < words.length; i++)
			{
				String word = words[i].split("/")[0];
				String pos = words[i].split("/")[1];
				if (i == 2)
				{
					if (words[i - 1].split("/")[1].equals("nr") && pos.equals("n")
							&& words[i - 1].split("/")[0].length() <= 2)
					{
						dict.add(word);
					}
				}
				else if (i > 2)
				{
					if (!(words[i - 2].split("/")[1].equals("nr")) && words[i - 1].split("/")[1].equals("nr")
							&& pos.equals("n") && words[i - 1].split("/")[0].length() <= 2)
					{
						dict.add(word);
					}
				}
			}
		}

		data.close();

		return dict;
	}

	/**
	 * 得到最好的numTaggings个标记序列
	 * 
	 * @param numTaggings
	 *            个数
	 * @param characters
	 *            一个个词语
	 * @param pos
	 *            词性标注
	 * @return 分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] characters, String[] pos)
	{
		Sequence[] bestSequences = model.bestSequences(numTaggings, characters, pos, contextGenerator,
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
	 *            一个个词语
	 * @param pos
	 *            词性标注
	 * @return
	 */
	public Sequence[] topKSequences(String[] characters, String[] pos)
	{
		return this.topKSequences(characters, pos, null);
	}

	/**
	 * 最好的K个序列
	 * 
	 * @param characters
	 *            一个个词语
	 * @param pos
	 *            词性标注
	 * @param additionaContext
	 * @return
	 */
	public Sequence[] topKSequences(String[] characters, String[] pos, Object[] additionaContext)
	{
		return model.bestSequences(size, characters, pos, contextGenerator, sequenceValidator);
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
	 * 读入一句词性标注的语料，得到最终结果
	 * 
	 * @param sentence
	 *            读取的生语料
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String sentence)
	{
		String[] str = sentence.split("\\s+");
		List<String> words = new ArrayList<>();
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < str.length; i++)
		{
			String word = str[i].split("/")[0];
			String tag = str[i].split("/")[1];
			words.add(word);
			tags.add(tag);
		}
		return ner(words.toArray(new String[words.size()]), tags.toArray(new String[tags.size()]));
	}

	/**
	 * 读入词性标注的语料，得到命名实体
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String[] words, String[] poses)
	{
		String[] tags = tag(words, poses, null);
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
				ners.add(getNer(0, tags, words, flag));
			}
			else
			{
				ners.add(getNer(i, tags, words, flag));
			}
			i = ners.get(ners.size() - 1).getEnd();
		}
		return ners.toArray(new NamedEntity[ners.size()]);
	}

	/**
	 * 读入一句词性标注的语料，得到指定的命名实体
	 * 
	 * @param sentence
	 *            读取的词性标注的语料
	 * @param flag
	 *            命名实体标记
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String sentence, String flag)
	{
		String[] str = sentence.split("\\s+");
		List<String> words = new ArrayList<>();
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < str.length; i++)
		{
			String word = str[i].split("/")[0];
			String tag = str[i].split("/")[1];
			words.add(word);
			tags.add(tag);
		}
		return ner(words.toArray(new String[words.size()]), tags.toArray(new String[tags.size()]), flag);
	}

	/**
	 * 读入词性标注的语料，得到指定的命名实体
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @param flag
	 *            命名实体标记
	 * @return
	 */
	@Override
	public NamedEntity[] ner(String[] words, String[] poses, String flag)
	{
		NamedEntity[] ners = ner(words, poses);
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
	 * 读入一句词性标注的语料，得到最终结果
	 * 
	 * @param sentence
	 *            读取的词性标注的语料
	 * @return
	 */
	public NamedEntity[][] ner(String sentence, int k)
	{
		String[] str = sentence.split("\\s+");
		List<String> words = new ArrayList<>();
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < str.length; i++)
		{
			String word = str[i].split("/")[0];
			String tag = str[i].split("/")[1];
			words.add(word);
			tags.add(tag);
		}
		return ner(words.toArray(new String[words.size()]), tags.toArray(new String[tags.size()]), k);
	}

	/**
	 * 读入词性标注的语料，得到命名实体
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @return
	 */
	public NamedEntity[][] ner(String[] words, String[] poses, int k)
	{
		String[][] tags = tag(k, words, poses);
		NamedEntity[][] kners = new NamedEntity[k][];
		for (int i = 0; i < tags.length; i++)
		{
			List<NamedEntity> ners = new ArrayList<>();
			for (int j = 0; j < tags[i].length; j++)
			{
				String flag;
				if (tags[i][j].equals("o"))
				{
					flag = "o";
				}
				else
				{
					flag = tags[i][j].split("_")[1];
				}
				if (ners.size() == 0)
				{
					ners.add(getNer(0, tags[i], words, flag));
				}
				else
				{
					ners.add(getNer(j, tags[i], words, flag));
				}
				j = ners.get(ners.size() - 1).getEnd();
			}
			kners[i] = ners.toArray(new NamedEntity[ners.size()]);
		}
		return kners;
	}
}
