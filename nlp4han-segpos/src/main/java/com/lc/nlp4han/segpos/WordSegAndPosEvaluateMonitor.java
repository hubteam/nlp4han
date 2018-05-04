package com.lc.nlp4han.segpos;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 评估监控器
 * @author 王馨苇
 *
 */
public class WordSegAndPosEvaluateMonitor implements EvaluationMonitor<WordSegAndPosSample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void correctlyClassified(WordSegAndPosSample arg0, WordSegAndPosSample arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void missclassified(WordSegAndPosSample arg0, WordSegAndPosSample arg1) {
		// TODO Auto-generated method stub
		
	}

}
