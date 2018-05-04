package com.lc.nlp4han.pos.ref;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.pos.POSTagger;
import com.lc.nlp4han.pos.WordPOSMeasure;
import com.lc.nlp4han.pos.word.WordPOSSample;
import com.lc.nlp4han.pos.word.WordPOSEvalTool;
import com.lc.nlp4han.pos.word.WordTagSampleStream;

/**
 * 基准词性标注交叉验证器
 * 
 * @author 刘小峰
 * 
 */
public class POSTaggerRefCrossValidatorTool
{
  
    private static void usage()
    {
        System.out.println(POSTaggerRefCrossValidatorTool.class.getName() + " -data <corpusFile> -encoding <encoding> [-folds <nFolds>]");
    }
    
    public static POSModelRef train(ObjectStream<WordPOSSample> samples) throws IOException
    {
        WordPOSSample sample = null;
        HashMap<String, Integer> tag2Count = new HashMap<String, Integer>();
        HashMap<String, HashMap<String, Integer>> word2TagCount = new HashMap<String, HashMap<String, Integer>>();
        while ((sample = samples.read()) != null)
        {
            String[] words = sample.getSentence();
            String[] tags = sample.getTags();

            for (int i = 0; i < words.length; i++)
            {
                String word = words[i];
                String tag = tags[i];

                int tagCount = 1;
                if (tag2Count.containsKey(tag))
                {
                    tagCount = tag2Count.get(tag);
                    tagCount++;
                    tag2Count.put(tag, tagCount);
                }
                else
                    tag2Count.put(tag, tagCount);

                if (word2TagCount.containsKey(word))
                {
                    HashMap<String, Integer> tag2CountByWord = word2TagCount.get(word);

                    int tagCountByWord = 1;
                    if (tag2CountByWord.containsKey(tag))
                    {
                        tagCountByWord = tag2CountByWord.get(tag);
                        tagCountByWord++;
                        tag2CountByWord.put(tag, tagCountByWord);
                    }
                    else
                        tag2CountByWord.put(tag, tagCountByWord);
                }
                else
                {
                    HashMap<String, Integer> tag2CountByWord = new HashMap<String, Integer>();
                    tag2CountByWord.put(tag, 1);
                    word2TagCount.put(word, tag2CountByWord);
                }

            }

        }

        Map.Entry<String, Integer> e = biggestEntry(tag2Count);

        String mostFreqTag = "";
        if (e != null)
            mostFreqTag = e.getKey();

        HashMap<String, String> word2MostFreqTag = new HashMap<String, String>();
        for (Map.Entry<String, HashMap<String, Integer>> element : word2TagCount.entrySet())
        {
            String word = element.getKey();
            HashMap<String, Integer> counts = element.getValue();

            e = biggestEntry(counts);
            String freqTag = "";
            if (e != null)
                freqTag = e.getKey();

            word2MostFreqTag.put(word, freqTag);
        }

        return new POSModelRef(word2MostFreqTag, mostFreqTag);
    }
    

    private static Map.Entry<String, Integer> biggestEntry(HashMap<String, Integer> elements)
    {
        Map.Entry<String, Integer> result = null;
        for (Map.Entry<String, Integer> e : elements.entrySet())
        {
            if (result == null)
            {
                result = e;
                continue;
            }

            if (e.getValue() > result.getValue())
                result = e;
        }

        return result;
    }
    
    public void evaluate(ObjectStream<WordPOSSample> samples, int nFolds) throws IOException
    {

        CrossValidationPartitioner<WordPOSSample> partitioner = new CrossValidationPartitioner<>(samples, nFolds);

        while (partitioner.hasNext())
        {
            CrossValidationPartitioner.TrainingSampleStream<WordPOSSample> trainingSampleStream = partitioner.next();

            System.out.println("构建词典...");
            HashSet<String> dict = WordPOSEvalTool.buildDict(trainingSampleStream);

            System.out.println("训练模型...");
            trainingSampleStream.reset();
            long start = System.currentTimeMillis();
            POSModelRef model = train(trainingSampleStream);
            System.out.println("训练时间： " + (System.currentTimeMillis()-start));

            System.out.println("评价模型...");
            POSTagger tagger = new POSTaggerRef(model);

            POSTaggerRefEvalTool evaluator = new POSTaggerRefEvalTool(tagger);

            WordPOSMeasure measure = new WordPOSMeasure(dict);
            evaluator.setMeasure(measure);

            start = System.currentTimeMillis();
            evaluator.evaluate(trainingSampleStream.getTestSampleStream());
            System.out.println("标注时间： " + (System.currentTimeMillis()-start));

            System.out.println(evaluator.getMeasure());
        }
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        int folds = 10;
        File corpusFile = null;
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            } 
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        
        POSTaggerRefCrossValidatorTool crossValidator = new POSTaggerRefCrossValidatorTool();

        String seperator = "_";

        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        ObjectStream<WordPOSSample> sampleStream = new WordTagSampleStream(lineStream, seperator);

        crossValidator.evaluate(sampleStream, folds);
    }
}
