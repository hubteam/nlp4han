package com.lc.nlp4han.pos.character;


import com.lc.nlp4han.ml.util.Evaluator;
import com.lc.nlp4han.pos.WordPOSMeasure;


/**
 * 评估器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class CharPOSEvaluator extends Evaluator<CharPOSSample>{

	private CharPOSTaggerME tagger;
	private WordPOSMeasure measure;
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 */
	public CharPOSEvaluator(CharPOSTaggerME tagger) {
		this.tagger = tagger;
	}
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public CharPOSEvaluator(CharPOSTaggerME tagger,CharPOSEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.tagger = tagger;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(WordPOSMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public WordPOSMeasure getMeasure(){
		return this.measure;
	}
	
	/**
	 * 评估得到指标
	 * @param reference 参考的语料
	 */
	@Override
	protected CharPOSSample processSample(CharPOSSample reference) {
		String[] wordsRef = reference.getWords();
		String[] charactersRef = reference.getCharacters();
		String[] tagsRef = reference.getTagsAndPoses();
		String[] posesRef = CharPOSSample.toPos(tagsRef);
		String[] tagsandposesPre = tagger.tag(charactersRef,wordsRef);
		String[] posesPre = CharPOSSample.toPos(tagsandposesPre);

		CharPOSSample prediction = new CharPOSSample(charactersRef,wordsRef,tagsandposesPre);
		measure.updateScores(wordsRef,posesRef,posesPre);
		return prediction;
	}

}
