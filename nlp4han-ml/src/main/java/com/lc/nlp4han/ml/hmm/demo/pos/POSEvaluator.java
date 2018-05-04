package com.lc.nlp4han.ml.hmm.demo.pos;

import java.util.HashSet;
import java.util.List;

import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;


/**
 *<ul>
 *<li>Description: 词性标注评估
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年3月27日
 *</ul>
 */
public class POSEvaluator {

	private HMM model;
	private List<SupervisedHMMSample> samples;
	private HashSet<String> dict;
		
	public POSEvaluator(HMM model, HashSet<String> dict, List<SupervisedHMMSample> samples) {
		this.model = model;
		this.dict = dict;
		this.samples = samples;
	}
	
	public void eval() {
		EvaluateMeasure measure = new EvaluateMeasure(dict);
		for(SupervisedHMMSample sample : samples) {
			StateSequence refStateSeuence = sample.getStateSequence();
			ObservationSequence wordSequence = sample.getObservationSequence();
			
			StateSequence preStateSeuence = model.bestStateSeqence(wordSequence);
			String[] words = new String[wordSequence.length()];
			String[] refPOS = new String[refStateSeuence.length()];
			String[] prePOS = new String[refStateSeuence.length()];
			for(int i = 0; i < words.length; i++) {
				words[i] = wordSequence.get(i).toString();
				refPOS[i] = refStateSeuence.get(i).toString();
				prePOS[i] = preStateSeuence.get(i).toString();
			}
			measure.updateScores(words, refPOS, prePOS);
		}
						
		System.out.println(measure);
	}
}
