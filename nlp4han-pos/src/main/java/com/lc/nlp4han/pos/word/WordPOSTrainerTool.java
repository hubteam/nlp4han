package com.lc.nlp4han.pos.word;

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
 * 训练和保存最大熵基于词的词性标注模型
 *
 * @author 刘小峰
 */
public class WordPOSTrainerTool
{

    /**
     * 训练和保存最大熵基于词的词性标注模型
     *
     * @param corpusFile 训练语料库文件
     * @param modelFile 模型保存文件
     * @param params 最大熵模型训练参数
     * @param contextGenerator 训练上下文产生器
     * @param encoding 训练语料编码
     * 
     * @throws java.io.IOException
     */
    public static void train(File corpusFile, File modelFile, TrainingParameters params,
            AbstractStringContextGenerator contextGenerator, String encoding) throws IOException
    {
        // TODO: 词和词性间分隔符作为参数
        String seperator = "_";
        
        ModelWrapper model = null;

        OutputStream modelOut = null;
        try
        {
            ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
            ObjectStream<WordPOSSample> sampleStream = new WordTagSampleStream(lineStream, seperator);

            long start = System.currentTimeMillis();

            model = POSTaggerWordME.train(sampleStream, params, contextGenerator);

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
        System.out.println(WordPOSTrainerTool.class.getName() + " -data <corpusFile> -model <modelFile> -encoding <encoding> "
                + "[-cutoff <num>] [-iters <num>]");
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
        File corpusFile = null;
        File modelFile = null;
        String encoding = "UTF-8";
        String algType = "MAXENT";
        String contextClass = "com.lc.nlp4han.pos.word.DefaultWordPOSContextGenerator";
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
            else if (args[i].equals("-context"))
            {
                contextClass = args[i + 1];
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
