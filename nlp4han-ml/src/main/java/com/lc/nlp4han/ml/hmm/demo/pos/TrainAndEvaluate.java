package com.lc.nlp4han.ml.hmm.demo.pos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.lc.nlp4han.ml.hmm.io.AbstractHMMReader;
import com.lc.nlp4han.ml.hmm.io.BinaryFileHMMReader;
import com.lc.nlp4han.ml.hmm.io.BinaryFileHMMWriter;
import com.lc.nlp4han.ml.hmm.io.HMMWriter;
import com.lc.nlp4han.ml.hmm.io.ObjectFileHMMReader;
import com.lc.nlp4han.ml.hmm.io.ObjectFileHMMWriter;
import com.lc.nlp4han.ml.hmm.io.TextFileHMMReader;
import com.lc.nlp4han.ml.hmm.io.TextFileHMMWriter;
import com.lc.nlp4han.ml.hmm.learn.DefaultConvergencyJudge;
import com.lc.nlp4han.ml.hmm.learn.HMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedAdditionHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedGoodTuringHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedInterpolationHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedMLHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedWittenBellHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.UnSupervisedBaumWelchHMMTrainer;
import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.model.HMMWithAStar;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.model.HMModelByRandom;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.stream.UnSupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;
import com.lc.nlp4han.ml.hmm.utils.State;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;

/**
 *<ul>
 *<li>Description: 词性标注训练和评估
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年3月27日
 *</ul>
 */
public class TrainAndEvaluate {

	private final double DEFAULT_RATIO = 0.1;
	private int order;
	private final int DEFAULT_ORDER = 1;
	private double ratio;
	private String smooth;
	private List<SupervisedHMMSample> supervisedSamples;
	
	public TrainAndEvaluate(List<SupervisedHMMSample> supervisedSamples, int order, String smooth, double ratio) {
		this.supervisedSamples = supervisedSamples;
		this.order = order > 0 ? order : DEFAULT_ORDER;
		this.smooth = smooth;
		this.ratio = (ratio < 1.0 && ratio > 0.01) ? ratio : DEFAULT_RATIO;
	}
	
	/**
	 * 交叉验证
	 * @param order		模型阶数
	 * @param folds		交叉验证折数
	 * @throws IOException
	 */
	public void crossValidation(int order, int folds, boolean isSupervised) throws IOException {
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
			HMModel model = train(trainSamples, isSupervised);
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
	public HMModel train(List<SupervisedHMMSample> trainsamples, boolean isSupervised) throws IOException {
		HMMTrainer trainer = null;
		
		if(isSupervised) {
			switch (smooth.toUpperCase()) {
			case "ML":
				trainer = new SupervisedMLHMMTrainer(trainsamples, order);
				break;
			case "ADD":
				trainer = new SupervisedAdditionHMMTrainer(trainsamples, order);
				break;
			case "INT":
				trainer = new SupervisedInterpolationHMMTrainer(trainsamples, ratio, order);
				break;
			case "WB":
				trainer = new SupervisedWittenBellHMMTrainer(trainsamples, order);
				break;
			case "GT":
				trainer = new SupervisedGoodTuringHMMTrainer(trainsamples, order);
				break;
			default:
				throw new IllegalArgumentException("错误的平滑方法：" + smooth);
			}
		}else {
			HashSet<State> statesSet = new HashSet<>();
			HashSet<Observation> observationsSet = new HashSet<>();
			
			List<UnSupervisedHMMSample> trainSamples = new ArrayList<>();
			for(SupervisedHMMSample sample : trainsamples) {
				ObservationSequence observationSequence = sample.getObservationSequence();
				trainSamples.add(new UnSupervisedHMMSample(observationSequence));
				
				StateSequence stateSequence = sample.getStateSequence();
				for(int i = 0; i < stateSequence.length(); i++) {
					statesSet.add(stateSequence.get(i));
					observationsSet.add(observationSequence.get(i));
				}
			}
			
			trainer = new HMModelByRandom(observationsSet, statesSet, 3);
			System.out.println("已建立初始模型");
			HMModel model = trainer.train();
			trainer = new UnSupervisedBaumWelchHMMTrainer(model, trainSamples, new DefaultConvergencyJudge());
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
	public static void writeModel(HMModel model, File file, String type) throws IOException {
		HMMWriter writer = null;
		switch (type.toLowerCase()) {
		case "text":
			writer = new TextFileHMMWriter(model, file);
			break;
		case "binary":
			writer = new BinaryFileHMMWriter(model, file);
			break;
		case "object":
			writer = new ObjectFileHMMWriter(model, file);
			break;
		default:
			throw new IllegalArgumentException("错误的文件类型:text/binary/object");
		}
		
		writer.persist();
	}
	
	/**
	 * 读取模型
	 * @param modelFile	模型文件路径
	 * @return			HMM模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static HMModel loadModel(File modelFile, String type) throws IOException, ClassNotFoundException {
		AbstractHMMReader reader = new TextFileHMMReader(modelFile);
		switch (type.toLowerCase()) {
		case "text":
			reader = new TextFileHMMReader(modelFile);
			break;
		case "binary":
			reader = new BinaryFileHMMReader(modelFile);
			break;
		case "object":
			reader = new ObjectFileHMMReader(modelFile);
			break;
		default:
			throw new IllegalArgumentException("错误的文件类型:text/binary/object");
		}
		return reader.readModel();
	}
	
}
