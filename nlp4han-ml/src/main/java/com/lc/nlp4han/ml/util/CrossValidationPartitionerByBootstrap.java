package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 *<ul>
 *<li>Description: 自助法交叉验证，提供n折交叉验证的训练和测试数据
 *<li>自助法交叉验证，获取n折交叉验证数据，每折数据根据有放回抽样从中选取size（训练样本数）个数据作为训练数据，剩余样本作为测试数据。
 *CrossValidationPartitionerByBootstrap每次获取一个TrainingSampleStream，TestSampleStream可从TrainingSampleStream中获取。
 *
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年3月11日
 *</ul>
 */
public class CrossValidationPartitionerByBootstrap<E> {
	
	/**
	 * 所有训练样本，样本编号与样本的映射
	 */
	private HashMap<Integer, E> samples;
	
	/**
	 * 数据被分割的折数
	 */
	private final int nFolds;

	/**
	 * 当前折数
	 */
	private int currentFold;
	
	/**
	 * 训练样本
	 */
	private List<E> trainSamples;
		
	/**
	 * 总的样本数
	 */
	private int total;
	
	/**
	 * 用于训练的样本编号
	 */
	private List<Integer> trainIndexsList;
	
	/**
	 * 随机抽样的结果是否包含重复样本
	 */
	private final boolean duplicate;

	/**
	 * 构造函数，初始化当前实例
	 *
	 * @param inElements			样本数据流
	 * @param numberOfPartitions	被分割的块数
	 * @param duplicate				是否包含重复数据
	 */
	public CrossValidationPartitionerByBootstrap(ObjectStream<E> inElements, final int nFolds, final boolean duplicate) {
		if(nFolds < 1)
			throw new IllegalArgumentException("折数必须为正整数：numberOfPartitions = " + nFolds);
		
		this.nFolds = nFolds;
		this.duplicate = duplicate;
		this.samples = new HashMap<>();
		
		E elem = null;
		try {
			while(true) {
				elem = inElements.read();
				if(elem == null)
					break;
				
				samples.put(++total, elem);
			}
			
			if(total < nFolds)
				throw new IllegalArgumentException("样本过小，不足以支持" + nFolds + "折交叉验证 。 size = " + total);
			
			inElements.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构造函数，初始化当前实例
	 *
	 * @param elements				样本数据集合
	 * @param numberOfPartitions	被分割的块数
	 *  @param duplicate			是否包含重复数据
	 */
	public CrossValidationPartitionerByBootstrap(Collection<E> elements, final int numberOfPartitions, final boolean duplicate) {
		this(new CollectionObjectStream<E>(elements), numberOfPartitions, duplicate);
	}

	/**
	 * 返回是否还有可执行的数据
	 * @return	true-有/false-没有
	 */
	public boolean hasNext() {
		return currentFold < nFolds;
	}

	/**
	 * 检索下一个训练和测试数据
	 */
	public TrainingSampleStream<E> next() throws IOException {
		if(hasNext()) {
			trainIndexsList = new ArrayList<>();
			trainSamples = new ArrayList<>();
			Random random = new Random();
			for(int i = 0; i < total; i++)
				trainIndexsList.add(random.nextInt(total) + 1);
			
			if(duplicate) {
				for(int index : trainIndexsList) 
					trainSamples.add(samples.get(index));
			}else {
				trainIndexsList = new ArrayList<>(new HashSet<>(trainIndexsList));
				for(int index : trainIndexsList) 
					trainSamples.add(samples.get(index));
			}
			
			if(trainSamples.size() == 0)
	    		throw new IllegalArgumentException("样本过少, 训练数据不能为0 ：trainSize = " + trainSamples.size());
			
			TrainingSampleStream<E> trainingSampleStream = new TrainingSampleStream<E>(trainSamples, samples.values());
			currentFold++;
			
			return trainingSampleStream;
		}else 
			throw new NoSuchElementException();
	}
	
	/**
	 *<ul>
	 *<li>Description: 测试样本流
	 *<li>Company: HUST
	 *<li>@author Sonly
	 *<li>Date: 2018年3月11日
	 *</ul>
	 */
	private static class TestSampleStream<E> implements ObjectStream<E> {
		
		private final Collection<E> samples;
		
		private Iterator<E> iterator;
		
		/**
		 * 构造方法
		 * @param sampleStream			样本流
		 * @param numberOfPartitions	交叉验证折数
		 * @param testIndex				当前折数
		 */
		private TestSampleStream(final Collection<E> samples) {
			this.samples = samples;
			
			reset();
		}

		public E read() throws IOException {
			if (iterator.hasNext())
				return iterator.next();
			else
				return null;
		}

		/**
	     * 重置流
	     */
		public void reset() {
			this.iterator = samples.iterator();
		}

		public void close() {
		}
	}

	/**
	 *<ul>
	 *<li>Description: 训练样本流
	 *<li>当CrossValidationPartitionerByBootstrap迭代到下一折后，该训练样本流将无法再次使用
	 *<li>Company: HUST
	 *<li>@author Sonly
	 *<li>Date: 2018年3月11日
	 *</ul>
	 */
	public static class TrainingSampleStream<E> implements ObjectStream<E> {
	
		private final List<E> trainSamplesList;

		private Iterator<E> iterator;
		
		private final Collection<E> testSamples;
		
	    /**
	     * 构造方法
	     * @param sampleStream			样本流
	     * @param numberOfPartitions	交叉验证折数
	     * @param testIndex				当前折数
	     */
	    TrainingSampleStream(final List<E> trainSamplesList, final Collection<E> testSamples) {    	
	    	this.trainSamplesList = trainSamplesList;
	    	this.testSamples = testSamples;
	    	
	    	reset();
	    }
	
	    public E read() throws IOException {
	    	if (iterator.hasNext())
	    		return iterator.next();
	    	else
	    		return null;
	    }

	    /**
	     * 重置流
	     */
	    public void reset() {
	    	this.iterator = trainSamplesList.iterator();
	    }

	    public void close() throws IOException {
	    }

	    /**
	     * 返回测试样本流，样本数据流除去训练数据的部分
	     * @return	测试样本流
	     * @throws IOException
	     */
	    public ObjectStream<E> getTestSampleStream() throws IOException {
	    	return new TestSampleStream<>(testSamples);
	    }
	}
 

	@Override
	public String toString() {
		return "At partition" + Integer.toString(currentFold) +
				" of " + Integer.toString(nFolds);
	}
}
