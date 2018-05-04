package com.lc.nlp4han.ner;

import com.lc.nlp4han.ml.util.EvaluationMonitor;
import com.lc.nlp4han.ner.word.NERWordOrCharacterSample;

public class NEREvaluateMonitor implements EvaluationMonitor<NERWordOrCharacterSample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void correctlyClassified(NERWordOrCharacterSample arg0, NERWordOrCharacterSample arg1) {
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void missclassified(NERWordOrCharacterSample arg0, NERWordOrCharacterSample arg1) {
		
	}

}
