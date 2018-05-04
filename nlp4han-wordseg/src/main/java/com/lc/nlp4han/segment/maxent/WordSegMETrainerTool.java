package com.lc.nlp4han.segment.maxent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.ml.util.AbstractStringContextGenerator;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 训练和保存最大熵分词模型
 *
 * @author 刘小峰
 */
public class WordSegMETrainerTool
{

    /**
     * 训练和保存最大熵分词模型
     *
     * @param corpusFile 分词训练语料库文件
     * @param modelFile 分词模型保存文件
     * @param params 最大熵模型训练参数
     * @param contextGenerator 训练上下文产生器
     * @param encoding 分词训练语料编码
     * 
     * @throws java.io.IOException
     */
    public static void train(File corpusFile, File modelFile, TrainingParameters params,
            AbstractStringContextGenerator contextGenerator, String encoding) throws IOException
    {
        ModelWrapper model = null;

        OutputStream modelOut = null;
        try
        {
            ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
            ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);

            long start = System.currentTimeMillis();

            model = WordSegmenterME.train(sampleStream, params, contextGenerator);

            long t = System.currentTimeMillis() - start;
            System.out.println("Time for training: " + t);

            modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));

            model.serialize(modelOut);
        } finally
        {
            if (modelOut != null)
            {
                try
                {
                    modelOut.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void usage()
    {
        System.out.println(WordSegMETrainerTool.class.getName() + " -data <corpusFile> -model <modelFile> -encoding <encoding> "
                + "[-context <contextGenClass>] [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
    {
        if (args.length < 1)
        {
            usage();
            
            return;
        }

        int cutoff = 3;
        int iters = 100;
        String contextClass = "com.lc.nlp4han.segment.maxent.WordSegContextGeneratorConf";
        File corpusFile = null;
        File modelFile = null;
        String encoding = "UTF-8";
        String algType = "MAXENT";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("-model"))
            {
                modelFile = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            } else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-context"))
            {
                contextClass = args[i + 1];
                i++;
            }else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
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

        AbstractStringContextGenerator contextGenerator = (AbstractStringContextGenerator) Class.forName(contextClass).newInstance();
        
        train(corpusFile, modelFile, params, contextGenerator, encoding);
    }
}
