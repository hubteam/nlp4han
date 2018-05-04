package com.lc.nlp4han.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


/**
 * 最大生成树算法
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class MaxSpanningTree {	
	
	/**
	 * 最大生成树
	 * 
	 * @param depMatrix 概率
	 * @return 样本表示的依存结果
	 */
	public static DependencySample getMaxTree(DependencyParseMatrix depMatrix){
		double[][] proba = depMatrix.getProba();
		String[][] dependency = depMatrix.getDependency();
		String[] sentence = depMatrix.getSentence();
		String[] pos = depMatrix.getPos();	
		
		//当前生成树到剩余各顶点最短边的权值
		double[] maxcost = new double[proba.length];
		//最大权重
		double maxWeightNotNull = -1, maxWeightNull = -1;
		//记录最大权重对应的节点的下标
		int recordNotNull = -1, recordNull = -1;
		
		//关系依赖词对应的下标
		String[] dependencyIndiceRec = new String[proba.length-1];
		for (int i = 0; i < dependencyIndiceRec.length; i++) {
			dependencyIndiceRec[i] = "-1";
		}
		
		//记录关系
		String[] dependencyRec = new String[proba.length-1];
		//依赖的词
		String[] dependencyWordsRec = new String[proba.length-1];
		
		//从第一个词的节点开始
		int start = 1;
		for (int i = 0; i < proba[start].length; i++) {
			maxcost[i] = proba[start][i];
		}
		
		int i = 1,j = 0;
		while(i < proba.length){
			while(j < proba.length){	
				if(i != j){	
					//一行有不是null的，取不是null的最大的一个概率
					if((maxcost[j] > maxWeightNotNull) && (dependency[i][j].compareTo("null") != 0)){
						maxWeightNotNull = maxcost[j];
						recordNotNull = j;
					}
					
					//针对于一行都是null的情况，都是null取最大的null
					if(maxcost[j] > maxWeightNull){
						maxWeightNull = maxcost[j];
						recordNull = j;
					}
				}
				j++;
			}
			
			//一行有不是null的情况
			if(recordNotNull != -1){
				dependencyIndiceRec[i-1] = String.valueOf(recordNotNull);
				dependencyRec[i-1] = dependency[i][recordNotNull];
				dependencyWordsRec[i-1] = sentence[recordNotNull];
			}else{
				//一行都是null的情况
				dependencyIndiceRec[i-1] = String.valueOf(recordNull);
				dependencyRec[i-1] = dependency[i][recordNull];		
				dependencyWordsRec[i-1] = sentence[recordNull];	
			}
			
			//复位
			recordNotNull = -1;
			recordNull = -1;
			maxWeightNotNull = -1;
			maxWeightNull = -1;
			if(i < proba.length - 1){
				for (int k = 0; k < proba.length; k++) {
					maxcost[k] = proba[i+1][k];
				}
			}
			
			i++;
			j=0;		
		}
		
		return new DependencySample(sentence, pos, dependencyRec, dependencyWordsRec, dependencyIndiceRec);
	}
	
	/**
	 * 生成最大生成树，同时避免环的出现
	 * 
	 * @param phraseProba 概率
	 * @return 样本的形式
	 */
	public static DependencySample getMaxTreeNoLoop(DependencyParseMatrix phraseProba){
		double[][] proba = phraseProba.getProba();
		String[][] dependency = phraseProba.getDependency();
		String[] sentence = phraseProba.getSentence();
		String[] pos = phraseProba.getPos();	
		
		//最大权重
		double maxWeightNotNull = -1,maxWeightNull = -1;
		//关系依赖词对应的下标
		String[] dependencyIndiceRec = new String[proba.length-1];
		for (int i = 0; i < dependencyIndiceRec.length; i++) {
			dependencyIndiceRec[i] = "-1";
		}
		
		//记录关系
		String[] dependencyRec = new String[proba.length-1];
		//依赖的词
		String[] dependencyWordsRec = new String[proba.length-1];
		//记录会出现环的下标数组
	    List<Integer> recordNotNullLoop = new ArrayList<Integer>();
	    List<Integer> recordNullLoop = new ArrayList<Integer>();
	    
		//记录最大权重对应的节点的下标
		int recordNotNull = -1, recordNull = -1;
		int i = 1, j = 0;
		while(i < proba.length){
			while(j < proba.length){	
				if(i != j){					
					//一行有不是null的，取不是null的最大的一个概率
					if((proba[i][j] > maxWeightNotNull) && (dependency[i][j].compareTo("null") != 0)
							&& !(recordNotNullLoop.contains(j))){
						maxWeightNotNull = proba[i][j];
						recordNotNull = j;
					}	
					
					//针对于一行都是null的情况，都是null取最大的null
					if(proba[i][j] > maxWeightNull && !(recordNullLoop.contains(j))){
						maxWeightNull = proba[i][j];
						recordNull = j;
					}
				}
				j++;
			}
			
			//一行有不是null的情况
			if(recordNotNull != -1){
				//在里面加入判断是否有环
				//每加入一条边都要去判断是否为环路
				dependencyIndiceRec[i-1] = String.valueOf(recordNotNull);
				
				//判断是否有环
				boolean isLoop = HasForestOrLoop.hasLoop(dependencyIndiceRec, recordNotNull);
				if(isLoop){
					dependencyRec[i-1] = dependency[i][recordNotNull];
					dependencyWordsRec[i-1] = sentence[recordNotNull];
					
					i++;
					j=0;
					//i++表示到达下一次词语，为其找依存关系，把上个词语会出现循环的下标清空，用来记录与当前词语依存会出现环路的下标
					
					recordNullLoop.clear();
					recordNotNullLoop.clear();
					
				}else{//有环
					recordNotNullLoop.add(recordNotNull);
					j=0;
				}
			}else{
				//一行都是null的情况
				dependencyIndiceRec[i-1] = String.valueOf(recordNull);
				boolean isLoop = HasForestOrLoop.hasLoop(dependencyIndiceRec, recordNull);
				if(isLoop){
					dependencyRec[i-1] = dependency[i][recordNull];		
					dependencyWordsRec[i-1] = sentence[recordNull];
					i++;j=0;
					//i++表示到达下一次词语，为其找依存关系，把上个词语会出现循环的下标清空，用来记录与当前词语依存会出现环路的下标
					recordNullLoop.clear();
					recordNotNullLoop.clear();
				}else{
					recordNullLoop.add(recordNull);
					j=0;
				}
			}
			
			//复位
			recordNotNull = -1;
			recordNull = -1;
			maxWeightNotNull = -1;
			maxWeightNull = -1;	
		}
		
		return new DependencySample(sentence, pos, dependencyRec, dependencyWordsRec, dependencyIndiceRec);
	}

	/**
	 * 从K个最好的结果中生成K棵最大生成树，同时避免环的出现
	 * 
	 * @param phraseProba 概率
	 * @return 样本的形式
	 */
	public static DependencyTree[] getKMaxTrees(int k, DependencyParseMatrix phraseProba){
		String[][] proba = phraseProba.getKProba();//获得概率
		String[] sentence = phraseProba.getSentence();//获得词语
		String[] pos = phraseProba.getPos();//获得词性
		
		//记录有依赖关系的词语数组 n*k 其中n为词语的个数，k为几棵树
		String[][] wordsDep = new String[proba.length-1][k];
		String[][] dep = new String[proba.length-1][k];
		String[][] depIndice = new String[proba.length-1][k];
		
		Queue<DepDatum> queue;

		//记录会出现环的下标数组
	    List<Integer> recordNotNullLoop = new ArrayList<Integer>();

	    String[] tempIndice = new String[proba.length-1];
	    for (int i = 0; i < tempIndice.length; i++) {
	    	tempIndice[i] = "-1";
		}
	    
		int i = 1, j2 = 0;
		while(i < proba.length){
			queue = getQueue(i, phraseProba);//为了排序用的
			
			//k的循环为了得到K个最好的结果
			while(j2 < k) {				
				DepDatum data = queue.poll();				
				depIndice[i-1][j2] = data.getWordIndex() + "";
				
				//把depIndice中的一列转成一行
				for (int l = 0; l < i; l++) {
					tempIndice[l] = depIndice[l][j2];
				}
				
				//判断是否是环路
				boolean isLoop = HasForestOrLoop.hasLoop(tempIndice, data.getWordIndex());
				//（1）无环路
				if(isLoop){
					wordsDep[i-1][j2] = data.getWord();
					dep[i-1][j2] = data.getDependency();
					if(j2 == k -1){//所有的列上都没有环路的时候
						i++;
						j2=0;
						recordNotNullLoop.clear();
						break;
					}					
					j2++;//下一列
					//recordNotNullLoop.clear();
				}else{//(2)有环路
					//recordNotNullLoop.add(data.getWordIndex());
			    }
			}	
		}
		
		DependencyTree[] depTrees = new DependencyTree[k];
		List<String> wordsList = new ArrayList<>();
		List<String> wordsDepList = new ArrayList<>();
		List<String> depIndiceList = new ArrayList<>();
		for (int l = 0; l < k; l++) {
			for (int l2 = 0; l2 < proba.length-1; l2++) {
				wordsList.add(wordsDep[l2][l]);
				wordsDepList.add(dep[l2][l]);
				depIndiceList.add(depIndice[l2][l]);
				
			}
			
			DependencySample sample = new DependencySample(sentence, pos,
					wordsDepList.toArray(new String[wordsDepList.size()]),
					wordsList.toArray(new String[wordsList.size()]),
					depIndiceList.toArray(new String[depIndiceList.size()]));
			
			DependencyTree parse = new DependencyTree();
			parse.setTree(sample);
			depTrees[l] = parse;
		}
		
		return depTrees;
	}
	
	/**
	 * 将proba数组中第i行从大到小进行排序
	 * 
	 * @param i 词语的下标变量
	 * @param phraseProba 概率对象
	 * @return
	 */
	private static Queue<DepDatum> getQueue(int i, DependencyParseMatrix phraseProba){
		String[][] proba = phraseProba.getKProba();//获得概率
		String[][] dependency = phraseProba.getKDependency();//获得关系
		String[] sentence = phraseProba.getSentence();//词语
		
		Queue<DepDatum> queue = new PriorityQueue<>();//为了排序用的
		int j = 0;
		while(j < proba.length){	
			if(i != j){	
				int indexk = 0;
				//在这个位置把一行所有的k*n个概率放入数组中
				String[] temp1 = proba[i][j].split("_");
				String[] temp2 = dependency[i][j].split("_");
					
				for (int l = 0; l < temp1.length && l < temp2.length; l++) {
					if(temp2[l].compareTo("null") != 0){
						//最大的K个概率就是队列最前面的几个，然后也能根据下标取到K个标记
						queue.add(new DepDatum(Double.parseDouble(temp1[l]), indexk++, temp2[l], j, sentence[j]));
					}				
				}
			}
			
			j++;
		}
		
		return queue;
	}
}
