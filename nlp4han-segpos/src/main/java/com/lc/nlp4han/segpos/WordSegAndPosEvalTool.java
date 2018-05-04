package com.lc.nlp4han.segpos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 分词和词性标注评价程序
 * 
 * @author 刘小峰
 *
 */
public class WordSegAndPosEvalTool
{
    /**
     * 依据黄金标准评价标注效果
     * 
     * 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
     * 
     * @param modelFile
     *            系模型文件
     * @param goldFile
     *            黄标准文件
     * @param errorFile
     *            错误输出文件
     * @param encoding
     *            黄金标准文件编码
     * @throws IOException
     */
    public static void eval(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException
    {
//        System.out.println("构建词典...");
//        HashSet<String> dict = CorpusStat.buildDict(trainFile.toString(), encoding);

        System.out.println("训练模型...");  
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        WordSegAndPosParseStrategy parse = new WordSegAndPosParseOpen();
        ObjectStream<WordSegAndPosSample> sampleStream = new WordSegAndPosSampleStream(lineStream, parse);
        WordSegAndPosContextGenerator contextGen = new WordSegAndPosContextGeneratorConf();
        long start = System.currentTimeMillis();
        ModelWrapper model = WordSegAndPosME.train("zh", sampleStream, params, contextGen);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));
        
        System.out.println("构建词典...");
//        sampleStream = new WordSegAndPosSampleStream(lineStream, parse);
        sampleStream.reset();
        HashMap<String,List<String>> dict = WordSegAndPosME.bulidDictionary(sampleStream);

        System.out.println("评价模型...");
        WordSegAndPosME tagger = new WordSegAndPosME(model, contextGen);
        WordSegAndPosEvaluator evaluator;
        if (errorFile != null)
        {
            WordSegAndPosEvaluateMonitor errorMonitor = new WordSegAndPosErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new WordSegAndPosEvaluator(tagger, errorMonitor);
        }
        else
            evaluator = new WordSegAndPosEvaluator(tagger);
        
        WordSegAndPosMeasure measure = new WordSegAndPosMeasure(dict);
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordSegAndPosSample> testStream = new WordSegAndPosSampleStream(goldStream, parse);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }

    private static void usage()
    {
        System.out.println(WordSegAndPosEvalTool.class.getName() + " -data <trainFile> -gold <goldFile> -encoding <encoding> [-error <errorFile>]" + " [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        String trainFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
        int cutoff = 3;
        int iters = 100;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                trainFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-error"))
            {
                errorFile = args[i + 1];
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
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));

        if (errorFile != null)
        {
            eval(new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(new File(trainFile), params, new File(goldFile), encoding, null);
    }
}
