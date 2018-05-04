package com.lc.nlp4han.dependency;

import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 依存句法分析评估器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencyParseEvaluatorNoNull extends Evaluator<DependencySample>{

	private int forestcount = 0;
	private DependencyParserME tagger;
	private DependencyParseMeasure measure;

	/**
	 * 构造
	 * 
	 * @param tagger 模型和特征的组合结果
	 * @param monitors 评估的监听管理
	 */
	public DependencyParseEvaluatorNoNull(DependencyParserME tagger,DependencyParseEvaluateMonitor... monitor){
		super(monitor);
		this.tagger = tagger;
	}

	/**
	 * 加载用于计算各种指标的类
	 * 
	 * @param measure DependencyParsingMeasure对象
	 */
	public void setMeasure(DependencyParseMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 获取评价指标
	 * 
	 * @return 评价指标
	 */
	public DependencyParseMeasure getMeasure(){
		return measure;
	}
	
	/**
	 * 获取森林的个数
	 * 
	 * @return 森林的个数
	 */
	public int getForestCount(){
		return this.forestcount;
	}
	
	/**
	 * 调用evaluate方法时候自动激活执行，用于生成预测结果，并和参考的结果进行对比评估指标
	 * 
	 * @param sample 待评估的样本
	 */
	@Override
	protected DependencySample processSample(DependencySample sample) {
		String[] wordsRef = sample.getWords();
		String[] posRef = sample.getPos();
		String[] dependencyRef = sample.getDependency();
		String[] dependencyWordsRef = sample.getDependencyWords();
		
		//最大生成树
		DependencyParseMatrix proba = tagger.tagNoNull(wordsRef, posRef, sample.getAditionalContext());
		sample = MaxSpanningTree.getMaxTreeNoLoop(proba);
		
		//最大的K棵树
//		DependencyParsingBestProba p = tagger.tagK(3,wordsRef,posRef,sample.getAditionalContext());	
//		DependencyParserTree[] parser = new DependencyParserTree[3];
//		parser = MaxSpinningTree.getMaxFromKres(3, p);
//		sample = parser[0].getTree();
		
		String[] dependencyWordsPre = sample.getDependencyWords();
		String[] dependencyPre = sample.getDependency();
		String[] dependencyIndicesPre = sample.getDependencyIndices();

		boolean forest = HasForestOrLoop.hasForest(wordsRef, dependencyIndicesPre);
		if(forest){
			forestcount++;
		}
		
		measure.updateScore(dependencyWordsRef, dependencyRef, dependencyWordsPre, dependencyPre);
		
		DependencySample samplePre = new DependencySample(wordsRef, posRef, dependencyPre, dependencyWordsPre, dependencyIndicesPre);
		
		return samplePre;
	}

}
