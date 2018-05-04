package com.lc.nlp4han.dependency;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 依存分析评估监视器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencyParseEvaluateMonitor implements EvaluationMonitor<DependencySample>{

	/**
	 * 预测正确的时候执行
	 * @param ref 参考的结果
	 * @param predict 预测的结果
	 */
	public void correctlyClassified(DependencySample ref, DependencySample predict) {
		
	}

	/**
	 * 预测错误的时候执行
	 * @param ref 参考的结果
	 * @param predict 预测的结果
	 */
	public void missclassified(DependencySample ref, DependencySample predict) {
		
	}

}
