package com.lc.nlp4han.ml.hmm.learn;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.lc.nlp4han.ml.hmm.model.EmissionProbEntry;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.model.HMModelBasedMap;
import com.lc.nlp4han.ml.hmm.model.TransitionProbEntry;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSampleStream;
import com.lc.nlp4han.ml.hmm.utils.CommonUtils;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.State;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;

/**
 *<ul>
 *<li>Description: 基于GoodTuring的监督学习模型训练类器
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月10日
 *</ul>
 */
public class SupervisedGoodTuringHMMTrainer extends AbstractSupervisedHMMTrainer {
	
	private final static int DEFALUE_K = 7;
	/**
	 * 计数折扣的阈值
	 */
	private int K;
	
	public SupervisedGoodTuringHMMTrainer(SupervisedHMMSampleStream<?> sampleStream, int order, int K) throws IOException {
		super(sampleStream, order);
		this.K = K > 0 ? K : DEFALUE_K;
	}
	public SupervisedGoodTuringHMMTrainer(SupervisedHMMSampleStream<?> sampleStream, int order) throws IOException {
		this(sampleStream, order, DEFALUE_K);
	}
	
	public SupervisedGoodTuringHMMTrainer(List<SupervisedHMMSample> samples, int order, int K) throws IOException {
		super(samples, order);
		this.K = K > 0 ? K : DEFALUE_K;
	}
	public SupervisedGoodTuringHMMTrainer(List<SupervisedHMMSample> samples, int order) throws IOException {
		this(samples, order, DEFALUE_K);
	}
	
	@Override
	public HMModel train() {
		calcPi(counter);
		calcTransitionMatrix(counter);
		calcEmissionMatrix(counter);
		
		HMModel model = new HMModelBasedMap(order, counter.getDictionary(), pi, transitionMatrix, emissionMatrix);
		
		return model;
	}
	
	/**
	 * 计算初始概率矩阵（已确保概率之和为1，不需要归一化）
	 * @param counter	转移发射计数器
	 */	
	@Override
	protected void calcPi(TransitionAndEmissionCounter counter) {
		int N = dict.stateCount();
		int M = counter.getTotalStartStatesCount();
		
		Set<State> set = counter.getDictionary().getStates();
		for(State state : set) {
			int count = counter.getStartStateCount(state);
			double prob = (count + DEFAULT_DELTA) / (M + N * DEFAULT_DELTA);
			pi.put(state, Math.log10(prob));
		}
	}
	
	@Override
	protected void calcTransitionMatrix(TransitionAndEmissionCounter counter) {
		GoodTuringCounts goodTuringCounts = new GoodTuringCounts(counter.getTransitionCount(), order, K);
		
		Set<State> statesSet = counter.getDictionary().getStates();
		for(State state : statesSet) {//遍历所有隐藏状态，增加所有可能的一阶转移
			StateSequence start = new StateSequence(state);
			TransitionProbEntry entry = new TransitionProbEntry();
			for(State target : statesSet) {//计算分母
				int len = start.length();
				int r = counter.getTransitionCount(start, target);
				
				double prob = 0.0;
				if(r != 0)
					prob =  r * goodTuringCounts.getDiscountCoeff(len, r) / goodTuringCounts.getTotalByOrder(len);
				else
					prob = goodTuringCounts.getN1ByOrder(len) / goodTuringCounts.getTotalByOrder(len) / (Math.pow(statesSet.size(), len));
				entry.put(target, Math.log10(prob));
			}
			
			transitionMatrix.put(start, entry);
		}
		
		for(int i = 1; i < order; i++) {//遍历增加所有2-order阶的转移概率
			StateSequence[] sequences = transitionMatrix.keySet().toArray(new StateSequence[transitionMatrix.size()]);
			for(StateSequence sequence : sequences) {
				if(sequence.length() == i) {
					for(State state : statesSet) {
						StateSequence start = sequence.addLast(state);						
						TransitionProbEntry entry = new TransitionProbEntry();
						for(State target : statesSet) {//计算分母
							int len = start.length();
							int r = counter.getTransitionCount(start, target);
							
							double prob = 0.0;
							if(r != 0)
								prob =  r * goodTuringCounts.getDiscountCoeff(len, r) / goodTuringCounts.getTotalByOrder(len);
							else
								prob = goodTuringCounts.getN1ByOrder(len) / goodTuringCounts.getTotalByOrder(len) / (Math.pow(statesSet.size(), len));
							entry.put(target, Math.log10(prob));
						}
						
						transitionMatrix.put(start, entry);
					}
				}
			}
		}
	}
	
	/**
	 * 采用加1平滑方式计算发射概率矩阵:p=(C+1)/(M+N)（已确保概率之和为1，不需要归一化）
	 * @param counter	转移发射计数器
	 */	
	protected void calcEmissionMatrix(TransitionAndEmissionCounter counter) {
		Iterator<State> iterator = counter.emissionIterator();
		int N = counter.getDictionary().observationCount();//观测状态的类型数
		
		while(iterator.hasNext()) {//遍历所有发射
			State state = iterator.next();
			Iterator<Observation> observationsIterator = counter.iterator(state);
			int M = counter.getEmissionStateCount(state);//以state为发射起点的总数量
			
			EmissionProbEntry emissionProbEntry = new EmissionProbEntry();
			while(observationsIterator.hasNext()) {//计算当前状态的所有发射概率
				Observation observation = observationsIterator.next();
				int C = counter.getEmissionCount(state, observation);//当前发射的数量
				double prob = (C + DEFAULT_DELTA) / (M + N * DEFAULT_DELTA + DEFAULT_DELTA);
				emissionProbEntry.put(observation, Math.log10(prob));
			}

			emissionProbEntry.put(CommonUtils.UNKNOWN, Math.log10(DEFAULT_DELTA / (M + N * DEFAULT_DELTA)));
			emissionMatrix.put(state, emissionProbEntry);
		}//end while
	}
}