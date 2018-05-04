package com.lc.nlp4han.pos.ref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.ml.util.Evaluator;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.pos.CorpusStat;
import com.lc.nlp4han.pos.POSTagger;
import com.lc.nlp4han.pos.WordPOSMeasure;
import com.lc.nlp4han.pos.word.WordPOSSample;
import com.lc.nlp4han.pos.word.WordPOSTaggerEvaluationMonitor;
import com.lc.nlp4han.pos.word.WordPOSConfusionMatrixBuilder;
import com.lc.nlp4han.pos.word.WordPOSErrorPrinter;
import com.lc.nlp4han.pos.word.WordTagSampleStream;

/**
 * 基准词性标注评价
 * 
 * @author 刘小峰
 * 
 */
public class POSTaggerRefEvalTool extends Evaluator<WordPOSSample>
{

    private POSTagger tagger;

    private WordPOSMeasure measure;

    public POSTaggerRefEvalTool(POSTagger tagger, WordPOSTaggerEvaluationMonitor... listeners)
    {
        super(listeners);
        this.tagger = tagger;
    }

    public WordPOSMeasure getMeasure()
    {
        return measure;
    }
    
    public void setMeasure(WordPOSMeasure m)
    {
        this.measure = m;
    }

    /**
     * 根据参考词性标注样本进行评价
     * 
     * 
     * @param reference
     *            参考标注
     * 
     * @return 系统标注
     */
    @Override
    protected WordPOSSample processSample(WordPOSSample reference)
    {
        String predictedTags[] = tagger.tag(reference.getSentence());

        WordPOSSample predictions = new WordPOSSample(reference.getSentence(), predictedTags);

        String referenceTags[] = reference.getTags();
        measure.updateScores(reference.getSentence(), referenceTags, predictedTags);

        return predictions;
    }
    
    public static HashSet<String> buildDict(ObjectStream<WordPOSSample> samples) throws IOException
    {
        HashSet<String> dict = new HashSet<String>();
        
        WordPOSSample sample;
        while((sample=samples.read()) != null)
        {
            String[] words = sample.getSentence();
            
            for(String w : words)
                dict.add(w);
        }
        
        return dict;
    }

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
    public static void eval(File trainFile, File goldFile, File errorFile, String encoding) throws IOException
    {
        String seperator = "_";
        System.out.println("构建词典...");
        HashSet<String> dict = CorpusStat.buildDict(trainFile.toString(), encoding);

        System.out.println("训练模型...");
        long start = System.currentTimeMillis();
        POSModelRef model = POSTaggerRefTrainTool.train(trainFile, encoding);
        System.out.println("训练时间： " + (System.currentTimeMillis()-start));

        System.out.println("评价模型...");
        POSTagger tagger = new POSTaggerRef(model);
        WordPOSErrorPrinter errorMonitor = new WordPOSErrorPrinter(new FileOutputStream(errorFile));
        WordPOSConfusionMatrixBuilder matrixBuilder = new WordPOSConfusionMatrixBuilder();
        POSTaggerRefEvalTool evaluator = new POSTaggerRefEvalTool(tagger, errorMonitor, matrixBuilder); 
        WordPOSMeasure measure = new WordPOSMeasure(dict);
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordPOSSample> testStream = new WordTagSampleStream(goldStream, seperator);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis()-start));

        System.out.println(evaluator.getMeasure());
        
        System.out.println(matrixBuilder.getMatrix());
    }

    /**
     * 依据黄金标准评价
     * 
     * 各种评价指标结果会输出到控制台
     * 
     * @param modelFile
     *            系统模型文件
     * @param goldFile
     *            黄金标准文件
     * @param encoding
     *            黄金标准文件编码
     * @throws IOException
     */
    public static void eval(File trainFile, File goldFile, String encoding) throws IOException
    {
        System.out.println("构建词典...");
        HashSet<String> dict = CorpusStat.buildDict(trainFile.toString(), encoding);

        System.out.println("训练模型...");
        POSModelRef model = POSTaggerRefTrainTool.train(trainFile, encoding);

        System.out.println("评价模型...");
        POSTagger tagger = new POSTaggerRef(model);
        POSTaggerRefEvalTool evaluator = new POSTaggerRefEvalTool(tagger); 
        WordPOSMeasure measure = new WordPOSMeasure(dict);
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordPOSSample> testStream = new WordTagSampleStream(goldStream, "_");

        evaluator.evaluate(testStream);

        System.out.println(evaluator.getMeasure());
    }

    private static void usage()
    {
        System.out.println(POSTaggerRefEvalTool.class.getName() + " -data <trainFile> -gold <goldFile> -encoding <encoding> [-error <errorFile>]");
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
        }
        

        if (errorFile != null)
        {
            eval(new File(trainFile), new File(goldFile), new File(errorFile), encoding);
        }
        else
            eval(new File(trainFile), new File(goldFile), encoding);
    }
}
