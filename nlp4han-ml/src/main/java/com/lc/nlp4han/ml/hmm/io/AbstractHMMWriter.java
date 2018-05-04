package com.lc.nlp4han.ml.hmm.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.lc.nlp4han.ml.hmm.model.EmissionProbEntry;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.model.TransitionProbEntry;
import com.lc.nlp4han.ml.hmm.utils.Dictionary;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.State;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;

/**
 *<ul>
 *<li>Description: 写HMM模型抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月14日
 *</ul>
 */
public abstract class AbstractHMMWriter implements HMMWriter {

	private int order;
	
	private Dictionary dictionary;
	
	private HashMap<State, Double> pi;
	
	private HashMap<StateSequence, TransitionProbEntry>  transitionMatrix;
	
	private HashMap<State, EmissionProbEntry>  emissionMatrix;
	
	private int[] counts;
	
	public AbstractHMMWriter(HMModel model) {
		order = model.getOrder();
		dictionary = model.getDict();
		pi = model.getPi();
		transitionMatrix = model.getTransitionMatrix();
		emissionMatrix = model.getEmissionMatrix();
		counts = new int[6];
		
		statCount();
	}

	/**
	 * 统计各个条目的数量
	 */
	private void statCount() {
		counts[0] = order;							//模型阶数
		counts[1] = dictionary.stateCount();		//隐藏状态数量
		counts[2] = dictionary.observationCount();	//观测状态数量
		counts[3] = pi.size();						//隐藏状态数量
		
		int total = 0;
		for(Entry<StateSequence, TransitionProbEntry> entry : transitionMatrix.entrySet())
			total += entry.getValue().size();
		counts[4] = total;							//转移条目数量
		
		total = 0;
		for(Entry<State, EmissionProbEntry> entry : emissionMatrix.entrySet())
			total += entry.getValue().size();
		
		counts[5] = total;							//发射条目数量
	}

	@Override
	public void persist() throws IOException {
		//写出各个条目的数量
		for(int count : counts)
			writeCount(count);
		
		//写出隐藏状态索引
		Set<State> statesSet = dictionary.getStates();
		for(State state : statesSet)
			writeStateIndex(new StateIndex(state, dictionary.getIndex(state)));
		
		//写出观测状态索引
		Set<Observation> observationsSet = dictionary.getObservations();
		for(Observation observation : observationsSet) 
			writeObservationIndex(new ObservationIndex(observation, dictionary.getIndex(observation)));
		
		//写出初始转移向量
		for(Entry<State, Double> entry : pi.entrySet())
			writePi(new PiEntry(entry.getKey(), entry.getValue()));
	
		//写出状态转移概率矩阵
		for(Entry<StateSequence, TransitionProbEntry> entry : transitionMatrix.entrySet()) {
			Iterator<Entry<State, Double>> iterator = entry.getValue().entryIterator();
			
			while(iterator.hasNext()) {
				Entry<State, Double> probEntry = iterator.next();
				writeTransitionMatrix(new TransitionEntry(entry.getKey(), probEntry.getKey(), probEntry.getValue()));
			}
		}
					
		//写出发射概率矩阵
		for(Entry<State, EmissionProbEntry> entry : emissionMatrix.entrySet()) {
			Iterator<Entry<Observation, Double>> iterator = entry.getValue().entryIterator();
					
			while(iterator.hasNext()) {
				Entry<Observation, Double> probEntry = iterator.next();
				writeEmissionMatrix(new EmissionEntry(entry.getKey(), probEntry.getKey(), probEntry.getValue()));
			}
		}
		
		close();
	}
}
