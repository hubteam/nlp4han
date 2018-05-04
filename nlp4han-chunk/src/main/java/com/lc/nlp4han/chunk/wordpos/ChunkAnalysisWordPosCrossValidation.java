package com.lc.nlp4han.chunk.wordpos;

import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析交叉验证 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisWordPosCrossValidation {
	
	/**
	 * 训练的参数集
	 */
	private final TrainingParameters params;
	
	/**
	 * 构造方法
	 * @param encoding	编码格式
	 * @param params	训练的参数
	 * @param monitor 	监听器
	 */
	public ChunkAnalysisWordPosCrossValidation(TrainingParameters params){
		this.params = params;
	}
	
	/**
	 * n折交叉验证评估
	 * @param sampleStream		样本流
	 * @param nFolds			折数
	 * @param contextGenerator	上下文生成器
	 * @param measure			组块分析评价器
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<AbstractChunkAnalysisSample> sampleStream, int nFolds, ChunkAnalysisContextGenerator contextGenerator, 
			AbstractChunkAnalysisMeasure measure, SequenceValidator<String> sequenceValidator) throws IOException{
		CrossValidationPartitioner<AbstractChunkAnalysisSample> partitioner = new CrossValidationPartitioner<AbstractChunkAnalysisSample>(sampleStream, nFolds);
		
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			String label = ((ChunkAnalysisWordPosSampleStream) sampleStream).getLabel();
			CrossValidationPartitioner.TrainingSampleStream<AbstractChunkAnalysisSample> trainingSampleStream = partitioner.next();
			HashSet<String> dict = getDict(trainingSampleStream);
			trainingSampleStream.reset();
			measure.setDictionary(dict);
			
			ChunkAnalysisWordPosME me = new ChunkAnalysisWordPosME();
			ModelWrapper model = me.train(trainingSampleStream, params, contextGenerator);
			ChunkAnalysisWordPosEvaluator evaluator = new ChunkAnalysisWordPosEvaluator(new ChunkAnalysisWordPosME(model, sequenceValidator, contextGenerator, label), measure);
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
	}
	
	/**
     * 获取词典
     * @param sampleStream	样本流
     * @return				词典
     * @throws IOException
     */
	private HashSet<String> getDict(ObjectStream<AbstractChunkAnalysisSample> sampleStream) throws IOException {
    	HashSet<String> dictionary = new HashSet<String>();
        AbstractChunkAnalysisSample sample = null;
        while ((sample = sampleStream.read()) != null) {
        	String[] words = sample.getTokens();
        	
        	for(String word : words)
        		dictionary.add(word);
		}
        
        return dictionary;
    }
}
