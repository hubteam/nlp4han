package com.lc.nlp4han.pos.character;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.pos.CorpusStat;
import com.lc.nlp4han.pos.WordPOSMeasure;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

/**
 * 基于字的最大熵词性标注评价
 * 
 * @author 刘小峰
 *
 */
public class CharPOSEvalTool
{
    /**
     * 依据黄金标准评价标注效果
     * 
     * 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
     * 
     * @param format
     *            解析文本的类
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
    public static void eval(String format, File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException
    {
        System.out.println("构建词典...");
        HashSet<String> dict = CorpusStat.buildDict(trainFile.toString(), encoding);

        System.out.println("训练模型...");  
        CharPOSSampleParser parse = null;
        ObjectStream<String> lineStream = null;
        if(format.equals("open")){
        	parse = new CharPOSParseOpen();
        	lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        }else if(format.equals("news")){
        	parse = new CharPOSParseNews();
        	lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        }
        
        ObjectStream<CharPOSSample> sampleStream = new CharPOSSampleStream(lineStream, parse);
        CharPOSContextGenerator contextGen = new CharPOSContextGeneratorConf();
        long start = System.currentTimeMillis();
        ModelWrapper model = CharPOSTaggerME.train(sampleStream, params, contextGen);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        CharPOSTaggerME tagger = new CharPOSTaggerME(model, contextGen);
        CharPOSEvaluator evaluator;
        CharPOSConfusionMatrixBuilder matrixBuilder = null;
        if (errorFile != null)
        {
            CharPOSEvaluateMonitor errorMonitor = new CharPOSErrorPrinter(new FileOutputStream(errorFile));
            matrixBuilder = new CharPOSConfusionMatrixBuilder();
            evaluator = new CharPOSEvaluator(tagger, errorMonitor, matrixBuilder);
        }
        else
            evaluator = new CharPOSEvaluator(tagger);
        
        WordPOSMeasure measure = new WordPOSMeasure(dict);
        evaluator.setMeasure(measure);
        
        ObjectStream<String> goldStream = null;
        if(format.equals("open")){
        	parse = new CharPOSParseOpen();
        	goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        }else if(format.equals("news")){
        	parse = new CharPOSParseNews();
        	goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        }
        
        ObjectStream<CharPOSSample> testStream = new CharPOSSampleStream(goldStream, parse);
        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
        
        if(matrixBuilder!=null)
            System.out.println(matrixBuilder.getMatrix());
    }

    private static void usage()
    {
        System.out.println(CharPOSEvalTool.class.getName() + " -data <trainFile> -gold <goldFile> -format <format> -encoding <encoding> [-error <errorFile>]" + " [-cutoff <num>] [-iters <num>]");
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
        String format = "open";
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
            else if (args[i].equals("-format"))
            {
            	format = args[i + 1];
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
            eval(format, new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(format, new File(trainFile), params, new File(goldFile), encoding, null);
    }
}
