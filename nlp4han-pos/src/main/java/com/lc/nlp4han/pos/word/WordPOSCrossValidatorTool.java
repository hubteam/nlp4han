package com.lc.nlp4han.pos.word;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.ml.util.AbstractStringContextGenerator;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.pos.WordPOSMeasure;

/**
 * 词性标注交叉验证器
 * 
 * @author 刘小峰
 * 
 */
public class WordPOSCrossValidatorTool
{
    private TrainingParameters params;

    private WordPOSTaggerEvaluationMonitor[] listeners;

    public WordPOSCrossValidatorTool(TrainingParameters trainParam, WordPOSTaggerEvaluationMonitor... listeners)
    {
        this.params = trainParam;
        this.listeners = listeners;
    }

    public void evaluate(ObjectStream<WordPOSSample> samples, int nFolds, 
            AbstractStringContextGenerator contextGenerator) throws IOException
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
            ModelWrapper model = POSTaggerWordME.train(trainingSampleStream, params, contextGenerator);
            System.out.println("训练时间： " + (System.currentTimeMillis()-start));

            System.out.println("评价模型...");
            POSTaggerWordME tagger = new POSTaggerWordME(model);

            WordPOSEvalTool evaluator = new WordPOSEvalTool(tagger, listeners);

            WordPOSMeasure measure = new WordPOSMeasure(dict);
            evaluator.setMeasure(measure);

            start = System.currentTimeMillis();
            evaluator.evaluate(trainingSampleStream.getTestSampleStream());
            System.out.println("标注时间： " + (System.currentTimeMillis()-start));

            System.out.println(evaluator.getMeasure());
        }
    }
    
    private static void usage()
    {
        System.out.println(WordPOSCrossValidatorTool.class.getName() + " -data <corpusFile> -encoding <encoding> [-folds <nFolds>] " + "[-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        File corpusFile = null;
        String encoding = "UTF-8";
        String algType = "MAXENT";
        String contextClass = "com.lc.nlp4han.pos.word.DefaultWordPOSContextGenerator";
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
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-context"))
            {
                contextClass = args[i + 1];
                i++;
            }
            else if (args[i].equals("-type"))
            {
                algType = args[i + 1];
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, algType);
        
        WordPOSCrossValidatorTool crossValidator = new WordPOSCrossValidatorTool(params);

        // TODO: 词和词性间分隔符作为参数
        String seperator = "_";
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        ObjectStream<WordPOSSample> sampleStream = new WordTagSampleStream(lineStream, seperator);

        AbstractStringContextGenerator contextGenerator = (AbstractStringContextGenerator) Class.forName(contextClass).newInstance();

        crossValidator.evaluate(sampleStream, folds, contextGenerator);
    }
}
