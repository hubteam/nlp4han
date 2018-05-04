package com.lc.nlp4han.ml.hmm.learn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.ml.hmm.model.BackwardAlgorithm;
import com.lc.nlp4han.ml.hmm.model.EmissionProbEntry;
import com.lc.nlp4han.ml.hmm.model.ForwardAlgorithm;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.model.HMModelBasedMap;
import com.lc.nlp4han.ml.hmm.model.TransitionProbEntry;
import com.lc.nlp4han.ml.hmm.stream.UnSupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.stream.UnSupervisedHMMSampleStream;
import com.lc.nlp4han.ml.hmm.utils.Dictionary;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;
import com.lc.nlp4han.ml.hmm.utils.State;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;

/**
 *<ul>
 *<li>Description: 基于Baum-Welch的非监督HMM训练器(目前只支持1阶HMM训练)
 *<li>训练器需有初始模型，初始模型可以导入现有的模型，也可以随机生成
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月23日
 *</ul>
 */
public class UnSupervisedBaumWelchHMMTrainer extends AbstractUnSupervisedHMMTrainer {

	/**
	 * 迭代收敛判断，返回true时结束迭代
	 */
	private ConvergencyJudge convergencyJudge;
	
	/**
	 * 训练语料
	 */
	private List<ObservationSequence> trainSequences;
	
	/**
	 * 构造方法
	 * @param initHMModel		初始模型
	 * @param sampleStream		训练样本（观测样本）流
	 * @param convergencyJudge	收敛判断
	 * @throws IOException
	 */
	public UnSupervisedBaumWelchHMMTrainer(HMModel initHMModel, UnSupervisedHMMSampleStream<?> sampleStream, ConvergencyJudge convergencyJudge) throws IOException {
		super(initHMModel);
		this.convergencyJudge = convergencyJudge;
		trainSequences = new ArrayList<>();
		
		UnSupervisedHMMSample sample = null;
		while((sample = (UnSupervisedHMMSample) sampleStream.read()) != null) {
			trainSequences.add(sample.getObservationSequence());
		}
		sampleStream.close();
	}
	public UnSupervisedBaumWelchHMMTrainer(HMModel initHMModel, UnSupervisedHMMSampleStream<?> sampleStream) throws IOException {
		this(initHMModel, sampleStream, new DefaultConvergencyJudge());
	}
	public UnSupervisedBaumWelchHMMTrainer(HMModel initHMModel, List<UnSupervisedHMMSample> trainSamples, ConvergencyJudge convergencyJudge) throws IOException {
		super(initHMModel);
		this.convergencyJudge = convergencyJudge;
		
		trainSequences = new ArrayList<>();
		for(UnSupervisedHMMSample sample : trainSamples)
			trainSequences.add(sample.getObservationSequence());
	}
	public UnSupervisedBaumWelchHMMTrainer(HMModel initHMModel, List<ObservationSequence> trainSequences) throws IOException {
		super(initHMModel);
		this.trainSequences = trainSequences;
		convergencyJudge = new DefaultConvergencyJudge();
	}
	
	@Override
	public HMModel train() {
		HMModel preModel, currentModel;
		currentModel = model;
		model = null;
		int iteration = 1;
		
		do{
			preModel = currentModel;
			currentModel = iterate(preModel, trainSequences);
		}while(!convergencyJudge.isConvergency(preModel, currentModel, trainSequences, iteration++));
		
		return currentModel;
	}
	
	/**
	 * 一次迭代，在当前HMM模型的基础上生成一个新的HMM模型
	 * @param model		当前模型
	 * @param sequences	训练语料(观测序列集)
	 * @return			新的HMM模型
	 */
	private HMModel iterate(HMModel model, final List<ObservationSequence> sequences) {
		Dictionary dict = model.getDict();
		HashMap<State, Double> pi = new HashMap<>();
		HashMap<StateSequence, TransitionProbEntry> transitionMatrix = new HashMap<>();
		HashMap<StateSequence, TransitionProbEntry> tempTransitionMatrix = model.getTransitionMatrix();
		HashMap<State, EmissionProbEntry> emissionMatrix = new HashMap<>();
		
		int N = model.statesCount();
		int M = model.observationsCount();
		
		double[][] 	alpha, beta;										//前向概率和后向概率
		double[] 	tempPiNumerator = new double[N];					//tempPiNumerator[i]初始转移为i的概率之和
		double 		tempPiDenominator = 0.0;							//所有初始转移之和的总和
		double[][] 	tempTransitionMatrixNumerator = new double[N][N];	//tempTransitionMatrixNumerator[i][j]由i转移到j的概率之和
		double[] 	tempTransitionMatrixDenominator = new double[N];	//tempTransitionMatrixDenominator[i]由i转移的概率之和
		double[][] 	tempEmissionMatrixNumerator = new double[N][M];		//tempEmissionMatrixNumerator[i][j]由i发射到j，且在训练语料中Ot=vj的概率之和
		double[] 	tempEmissionMatrixDenominator = new double[N];	//tempEmissionMatrixDenominator[i]由i发射的概率之和
		
		Arrays.fill(tempPiNumerator, 0.0);
		
		Arrays.fill(tempTransitionMatrixDenominator, 0.0);
		for(int i = 0; i < tempTransitionMatrixNumerator.length; i++)
			Arrays.fill(tempTransitionMatrixNumerator[i], 0.0);
		
		Arrays.fill(tempEmissionMatrixDenominator, 0.0);
		for(int i = 0; i < tempEmissionMatrixNumerator.length; i++)
			Arrays.fill(tempEmissionMatrixNumerator[i], 0.0);
		
		
		BackwardAlgorithm backward = null;
		ForwardAlgorithm forward = null;
		for(int no = 0; no < trainSequences.size(); no++) {
			ObservationSequence sequence = trainSequences.get(no);
			
			forward = new ForwardAlgorithm(model, sequence);
			alpha = forward.getAlpha();
			backward = new BackwardAlgorithm(model, sequence);
			beta = backward.getBeta();
			
			int T = sequence.length();
			int[] observationsIndex = new int[T];
			for(int i = 0; i < observationsIndex.length; i++)//观测序列的索引序列
				observationsIndex[i] = dict.getIndex(sequence.get(i));
			
			double[][][] xi = calcXi(model, observationsIndex, alpha, beta);
			double[][] gamma = calcGamma(model, T, alpha, beta);
			
			for(int i = 0; i < N; i++) {//遍历所有隐藏状态
				//计算初始转移概率
				tempPiNumerator[i] += gamma[0][i];
				tempPiDenominator += tempPiNumerator[i];
				
				//计算转移概率
				for(int t = 0; t < T - 1; t++) {
					tempTransitionMatrixDenominator[i] += gamma[t][i];
					
					for(int j = 0; j < N; j++) 
						tempTransitionMatrixNumerator[i][j] += xi[t][i][j];
				}
			
				//计算发射概率
				for(int t = 0; t < T; t++) {
					for(int k = 0; k < M; k++) {//遍历所有观测
						tempEmissionMatrixDenominator[i] += gamma[t][i];
						
						if(sequence.get(t).equals(dict.getObservation(k)))
							tempEmissionMatrixNumerator[i][k] += gamma[t][i];
					}
				}
			}
		}//训练语料遍历结束
		
		/**
		 * 重新估算模型参数
		 */		
		double prob = 0.0;
		for(int i = 0; i < N; i++) {
			State state = dict.getState(i);
			
			//计算初始转移概率
			prob = 0.001 + 0.999 * tempPiNumerator[i] / tempPiDenominator;
			pi.put(state, Math.log10(prob));
			
			//计算转移概率
			StateSequence start = new StateSequence(state);
			TransitionProbEntry transitionProbEntry = new TransitionProbEntry();
			for(int j = 0; j < N; j++) {
				State target = dict.getState(j);
				if(tempTransitionMatrixDenominator[i] == 0)
					prob = Math.pow(10, tempTransitionMatrix.get(start).getTransitionLogProb(target));
				else
					prob = 0.001 + 0.999 * tempTransitionMatrixNumerator[i][j] / tempTransitionMatrixDenominator[i];
				
				transitionProbEntry.put(target, Math.log10(prob));
			}
			transitionMatrix.put(start, transitionProbEntry);
		
			//计算发射概率
			EmissionProbEntry emissionProbEntry = new EmissionProbEntry();
			for(int j = 0; j < M; j++) {
				Observation observation = dict.getObservation(j);
				prob = 0.001 + 0.999 * tempEmissionMatrixNumerator[i][j] / tempEmissionMatrixDenominator[i];
				emissionProbEntry.put(observation, Math.log10(prob));
			}
			emissionMatrix.put(state, emissionProbEntry);
		}

		return new HMModelBasedMap(1, dict, pi, transitionMatrix, emissionMatrix);
	}
	
	/**
	 * 给定模型和观测，计算在t时刻处于状态i的概率gamma[t][i]
	 * gamma[t][i] = SUMj{xi[t][i][j]}
	 * @param model	HMM模型
	 * @param xi	
	 * @return		gamma
	 */
	private double[][] calcGamma(HMModel model, int T, double[][] alpha, double[][] beta) {
		if(T <= 1)
			throw new IllegalArgumentException("观测序列太短");

		int N = model.statesCount();
		double[][] gamma = new double[T][N];

		for(int t = 0; t < T; t++) {
			double normalization_factor = 0.0;
			for(int i = 0; i < N; i++) {
				gamma[t][i] = Math.pow(10, alpha[t][i]) * Math.pow(10, beta[t][i]);
				normalization_factor += gamma[t][i];
			}

			//归一化
			for(int i = 0; i < N; i++)
				gamma[t][i] /= normalization_factor;
		}

		return gamma;
	}
	
	/**
	 * 给定模型和观测，计算在t时刻处于状态i，且在t+1时刻处于状态j的概率xi[t][i][j]
	 * xi[t][i][j] = alpha[t][i]*A[i][j]*B[j][t+1]*beta[j][t+1]/denominator
	 * @param model			HMM模型
	 * @param T				观测序列长度
	 * @param observations	观测序列
	 * @param alpha			前向概率
	 * @param beta			后向概率
	 * @return				xi
	 */
	private double[][][] calcXi(HMModel model, int[] observations, double[][] alpha, double[][] beta) {
		int T = observations.length;
		if(T <= 1)
			throw new IllegalArgumentException("观测序列太短:" + model.getDict().getObservationSequence(observations));

		int N = model.statesCount();
		double[][][] xi = new double[T - 1][N][N];

		for(int t = 0; t < T - 1; t++) {
			double normalization_factor = 0.0;
			for(int i = 0; i < N; i++) {
				for(int j = 0; j < N; j++) {
					xi[t][i][j] = Math.pow(10, alpha[t][i]) * Math.pow(10, beta[t+1][j]) * Math.pow(10, model.transitionLogProb(new int[]{i}, j)) * Math.pow(10, model.emissionLogProb(j, observations[t+1]));
					normalization_factor += xi[t][i][j];
				}
			}

			//归一化
			for(int i = 0; i < N; i++) {
				for(int j = 0; j < N; j++)
					xi[t][i][j]  /= normalization_factor;
			}
		}

		return xi;
	}
}