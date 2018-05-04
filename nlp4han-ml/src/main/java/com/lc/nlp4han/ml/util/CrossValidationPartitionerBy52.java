package com.lc.nlp4han.ml.util;

import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeSet;

/**
 * 5*2折交叉验证
 * @author 王馨苇
 *
 * @param <E>
 */
public class CrossValidationPartitionerBy52<E> {

	/**
	 * 样本流
	 */
	private ObjectStream<E> sampleStream;
	
	/**
	 * 数据被分割的折数
	 */
	private final int numberOfPartitions;
	
	/**
	 * 当前折数
	 */
	private int testIndex;
	
	/**
	 * 是否交换
	 */
	private boolean exchanged;
	
	/**
	 * 用于训练的样本的索引
	 */
	private TreeSet<Integer> trainIndex;
	
	/**
	 * 总的样本数
	 */
	private int total;
	
	public CrossValidationPartitionerBy52(ObjectStream<E> inElements, int numberOfPartitions){
		this.sampleStream = inElements;
		this.numberOfPartitions = 2 * numberOfPartitions;
		
		E elem = null;
		try {
			while(true) {
				elem = inElements.read();
				if(elem == null)
					break;
				
				total++;
			}
			inElements.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CrossValidationPartitionerBy52(Collection<E> elements, int numberOfPartitions) {
		this((new CollectionObjectStream(elements)), numberOfPartitions);
	}
	
	public boolean hasNext() {
		return this.testIndex < this.numberOfPartitions;
	}
	
	public CrossValidationPartitionerBy52.TrainingSampleStream<E> next() throws IOException {
		if (this.hasNext()) {
			this.sampleStream.reset();
			
			CrossValidationPartitionerBy52.TrainingSampleStream<E> trainingSampleStream = null;
			
			if(!exchanged){
				trainIndex = new TreeSet<>();
				Random random = new Random();
				for (int i = 0; i < total / 2; i++) {
					trainIndex.add(random.nextInt(total) + 1);
				}
				trainingSampleStream = new CrossValidationPartitionerBy52.TrainingSampleStream<E>(sampleStream, trainIndex, total, exchanged);
				exchanged = true;
			}else{
				trainingSampleStream = new CrossValidationPartitionerBy52.TrainingSampleStream<E>(sampleStream, trainIndex, total, exchanged);
				exchanged = true;
			}
			
			++this.testIndex;
			return trainingSampleStream;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public String toString() {
		return "At partition" + Integer.toString(this.testIndex + 1) + " of "
				+ Integer.toString(this.numberOfPartitions);// 282 283
	}

	/**
	 * 训练样本流
	 * @author 王馨苇
	 *
	 * @param <E>
	 */
	public static class TrainingSampleStream<E> implements ObjectStream<E> {
		private ObjectStream<E> sampleStream;
		private int index;
		private int total;
		private TreeSet<Integer> trainIndex;
		private boolean exchanged;
		private CrossValidationPartitionerBy52.TestSampleStream<E> testSampleStream;

		private TrainingSampleStream(ObjectStream<E> sampleStream, final TreeSet<Integer> trainIndex, final int total, final boolean exchanged) {
			this.sampleStream = sampleStream;
			this.trainIndex = trainIndex;
			this.exchanged = exchanged;
			this.total = total;
		}

		public E read() throws IOException {
			if(exchanged) {//交换训练和测试数据
				while (trainIndex.contains(++index)) {//跳过测试样本
					sampleStream.read();
					
					if(index > total)
						return null;
						
				}

				return sampleStream.read();
			}else {//不交换训练和测试数据
				while (!trainIndex.contains(++index)) {//跳过测试样本
					sampleStream.read();
					
					if(index > total)
						return null;
				}

				return sampleStream.read();
			}
		}

		/**
		 * 重置流
		 */
		public void reset() throws IOException {
			if (this.testSampleStream == null) {
				this.index = 0;
				this.sampleStream.reset();
			} else {
				throw new IllegalStateException();
			}
		}

		/**
		 * 关闭流
		 */
		public void close() throws IOException {
			this.sampleStream.close();
		}

		/**
		 * 获取测试流
		 * @return
		 * @throws IOException
		 */
		public ObjectStream<E> getTestSampleStream() throws IOException {

			this.sampleStream.reset();
			this.testSampleStream = new CrossValidationPartitionerBy52.TestSampleStream<E>(sampleStream, trainIndex, total, exchanged);// 198
		    return this.testSampleStream;
		}
	}
	
	/**
	 * 获得测试样本
	 * @author 王馨苇
	 *
	 * @param <E>
	 */
	private static class TestSampleStream<E> implements ObjectStream<E> {
		private ObjectStream<E> sampleStream;
		private int index;
		private int total;
		private TreeSet<Integer> trainIndex;
		private boolean exchanged;
		
		/**
		 * 构造方法
		 * @param sampleStream			样本流
		 * @param trainIndexs			训练数据索引
		 */
		private TestSampleStream(ObjectStream<E> sampleStream, final TreeSet<Integer> trainIndex, final int total, final boolean exchanged) {
			this.sampleStream = sampleStream;
			this.trainIndex = trainIndex;
			this.exchanged = exchanged;
			this.total = total;
		}

		public E read() throws IOException {
			if(exchanged) {//交换训练和测试数据
				while (!trainIndex.contains(++index)) {//跳过训练样本
					sampleStream.read();
					
					if(index > total)
						return null;
				}

				return sampleStream.read();
			}else {//不交换训练和测试数据
				while (trainIndex.contains(++index)) {//跳过训练样本
					sampleStream.read();
					
					if(index > total)
						return null;
				}

				return sampleStream.read();
			}
		}

		/**
		 * 重置流
		 */
		public void reset() throws IOException {
			index = 0;
			this.sampleStream.reset();
		}

		public void close() throws IOException {
			sampleStream.close();
		}
	}
}
