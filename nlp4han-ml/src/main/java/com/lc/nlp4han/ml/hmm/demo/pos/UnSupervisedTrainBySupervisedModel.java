package com.lc.nlp4han.ml.hmm.demo.pos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.lc.nlp4han.ml.hmm.io.AbstractHMMReader;

import com.lc.nlp4han.ml.hmm.io.HMMWriter;
import com.lc.nlp4han.ml.hmm.io.TextFileHMMReader;
import com.lc.nlp4han.ml.hmm.io.TextFileHMMWriter;
import com.lc.nlp4han.ml.hmm.learn.HMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedAdditionHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedGoodTuringHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedMLHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedWittenBellHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.UnSupervisedBaumWelchHMMTrainer;
import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.model.HMMWithAStar;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;

/**
 *<ul>
 *<li>Description: 基于监督学习的模型作为初始模型的非监督学习
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年3月27日
 *</ul>
 */
public class UnSupervisedTrainBySupervisedModel {

	private final int DEFAULT_ORDER = 1;
	
	private int order;
	private String smooth;
	private List<SupervisedHMMSample> supervisedSamples;
	
	public UnSupervisedTrainBySupervisedModel(List<SupervisedHMMSample> supervisedSamples, String smooth) {
		this.supervisedSamples = supervisedSamples;
		this.order = order > 0 ? order : DEFAULT_ORDER;
		this.smooth = smooth;
	}

	/**
	 * 交叉验证
	 * @param order		模型阶数
	 * @param folds		交叉验证折数
	 * @throws IOException
	 */
	public void crossValidation(int order, int folds) throws IOException {
		if(folds < 1)
			throw new IllegalArgumentException("折数不能小于1：" + folds);
		System.out.println("cross validating...");

		for(int i = 0; i < folds; i++) {
			List<SupervisedHMMSample> trainSamples = new ArrayList<>();
			List<SupervisedHMMSample> testSamples = new ArrayList<>();
			int flag = 0;
			System.out.println("\nRunning : fold-" + (i + 1));
			for(SupervisedHMMSample sample : supervisedSamples) {
				if(flag % folds == i)
					testSamples.add(sample);
				else
					trainSamples.add(sample);
				
				flag++;
			}
			System.out.println("totalSize = " + supervisedSamples.size() + "\ttrainSize = " + trainSamples.size() + "\ttestSize = " + testSamples.size());
			long start = System.currentTimeMillis();
			HMModel model = initModel(trainSamples);
			List<ObservationSequence> trainSequences = new ArrayList<>();
			for(SupervisedHMMSample sample : trainSamples)
				trainSequences.add(sample.getObservationSequence());
			
			HMMTrainer trainer = new UnSupervisedBaumWelchHMMTrainer(model, trainSequences);
			model = trainer.train();
			long train = System.currentTimeMillis();
			evaluate(model, testSamples);
			long eval = System.currentTimeMillis();
			System.out.println("训练时间：" +(train - start)/1000.0 +"s\t评估时间："+(eval - train)/1000.0+"s");
		}
		
		System.out.println("cross validate over.");
	}
	
	/**
	 * 训练模型
	 * @param trainsamples	训练样本
	 * @return				HMM模型
	 * @throws IOException
	 */
	public HMModel initModel(List<SupervisedHMMSample> trainsamples) throws IOException {
		HMMTrainer trainer = null;

		switch (smooth.toUpperCase()) {
		case "ML":
			trainer = new SupervisedMLHMMTrainer(trainsamples, order);
			break;
		case "ADD":
			trainer = new SupervisedAdditionHMMTrainer(trainsamples, order);
			break;
		case "WB":
			trainer = new SupervisedWittenBellHMMTrainer(trainsamples, order);
			break;
		case "KATZ":
			trainer = new SupervisedGoodTuringHMMTrainer(trainsamples, order);
			break;
		default:
			throw new IllegalArgumentException("错误的平滑方法：" + smooth);
		}
		
		return trainer.train();
	}
	
	/**
	 * 测试评估
	 * @param hmModel		模型
	 * @param testSamples	测试样本（带词性）
	 */
	public void evaluate(HMModel hmModel, List<SupervisedHMMSample> testSamples) {
		HMM hmm = null;
//		hmm = new HMMWithViterbi(hmModel);
		hmm = new HMMWithAStar(hmModel);
		
		Observation[] observations = hmModel.getObservations();
		HashSet<String> dict = new HashSet<>();
		for(Observation observation : observations)
			dict.add(observation.toString());
				
		POSEvaluator evaluator = new POSEvaluator(hmm, dict, testSamples);
		evaluator.eval();
	}
	
	/**
	 * 写模型
	 * @param model		模型
	 * @param file		写出路径
	 * @throws IOException
	 */
	public static void writeModel(HMModel model, File file) throws IOException {
		HMMWriter writer = new TextFileHMMWriter(model, file);
		
		writer.persist();
	}
	
	/**
	 * 读取模型
	 * @param modelFile	模型文件路径
	 * @return			HMM模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static HMModel loadModel(File modelFile) throws IOException, ClassNotFoundException {
		AbstractHMMReader reader = new TextFileHMMReader(modelFile);
		
		return reader.readModel();
	}
	
}
