package com.lc.nlp4han.pos.character;

import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.pos.WordPOSMeasure;

/**
 * 交叉验证
 * 
 * @author 刘小峰
 * @author 王馨苇
 * 
 */
public class CharPOSCrossValidation
{

    private final TrainingParameters params;

    /**
     * 构造
     * 
     * @param languageCode
     *            编码格式
     * @param params
     *            训练的参数
     * @param listeners
     *            监听器
     */
    public CharPOSCrossValidation(TrainingParameters params)
    {
        this.params = params;
    }

    /**
     * 交叉验证十折评估
     * 
     * @param sample
     *            样本流
     * @param nFolds
     *            折数
     * @param contextGenerator
     *            上下文
     * @throws IOException
     *             io异常
     */
    public void evaluate(ObjectStream<CharPOSSample> sample, int nFolds, CharPOSContextGenerator contextGenerator) throws IOException
    {
        CrossValidationPartitioner<CharPOSSample> partitioner = new CrossValidationPartitioner<CharPOSSample>(sample, nFolds);
        int run = 1;
        // 小于折数的时候
        while (partitioner.hasNext())
        {
            System.out.println("Run" + run + "...");
            CrossValidationPartitioner.TrainingSampleStream<CharPOSSample> trainingSampleStream = partitioner.next();
            // 生成词典
            HashSet<String> dict = CharPOSTaggerME.buildDictionary(trainingSampleStream);
            
            // 训练模型
            trainingSampleStream.reset();
            long start = System.currentTimeMillis();
            ModelWrapper model = CharPOSTaggerME.train(trainingSampleStream, params, contextGenerator);
            System.out.println("训练时间： " + (System.currentTimeMillis()-start));

            CharPOSEvaluator evaluator = new CharPOSEvaluator(new CharPOSTaggerME(model, contextGenerator));
            WordPOSMeasure measure = new WordPOSMeasure(dict);
            evaluator.setMeasure(measure);
            // 设置测试集（在测试集上进行评价）
            start = System.currentTimeMillis();
            evaluator.evaluate(trainingSampleStream.getTestSampleStream());
            System.out.println("标注时间： " + (System.currentTimeMillis()-start));

            System.out.println(measure);
            run++;
        }
        // System.out.println(measure);
    }
}
