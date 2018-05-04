package com.lc.nlp4han.ml.hmm.stream;

import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;

/**
 *<ul>
 *<li>Description: 只包含观测状态序列的无监督学习样本类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月1日
 *</ul>
 */
public class UnSupervisedHMMSample extends AbstractHMMSample {
	
	public UnSupervisedHMMSample() {

	}

	public UnSupervisedHMMSample(ObservationSequence observationSequence) {
		super(observationSequence);
	}
	
	@Override
	public String toString() {
		String string = "[";
		
		for(int i = 0; i < observationSequence.length(); i++)
			string += observationSequence.get(i) + "  ";
		
		return string.trim() + "]";
	}
}
