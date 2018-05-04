package com.lc.nlp4han.ner.word;

import com.lc.nlp4han.ml.util.Evaluator;
import com.lc.nlp4han.ner.NEREvaluateMonitor;
import com.lc.nlp4han.ner.NERMeasure;

/**
 * 基于分词的命名实体分析评估器
 * @author 王馨苇
 *
 */
public class NERWordEvaluator extends Evaluator<NERWordOrCharacterSample>{

	private NERWordME tagger;
	private NERMeasure measure;
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 */
	public NERWordEvaluator(NERWordME tagger) {
		this.tagger = tagger;
	}
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public NERWordEvaluator(NERWordME tagger,NEREvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.tagger = tagger;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(NERMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public NERMeasure getMeasure(){
		return this.measure;
	}
	
	/**
	 * 评估得到指标
	 * @param reference 参考的语料
	 */
	@Override
	protected NERWordOrCharacterSample processSample(NERWordOrCharacterSample sample) {
		String[] wordsRef = sample.getWords();
		for (int i = 0; i < wordsRef.length; i++) {
			System.out.print(wordsRef[i]+" ");
		}
		String[] wordsAndtagsRef = sample.getTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] wordsAndtagsPre = tagger.tag(wordsRef, acRef);

		String[] tagsRef = NERWordOrCharacterSample.toNer(wordsAndtagsRef);
		String[] nerRef = NERWordOrCharacterSample.toWord(wordsRef, wordsAndtagsRef);
		String[] tagsPre = NERWordOrCharacterSample.toNer(wordsAndtagsPre);
		String[] nerPre = NERWordOrCharacterSample.toWord(wordsRef, wordsAndtagsPre);
		
		NERWordOrCharacterSample prediction = new NERWordOrCharacterSample(wordsRef, wordsAndtagsPre);
		measure.update(nerRef, tagsRef, nerPre, tagsPre);
		return prediction;
	}
}
