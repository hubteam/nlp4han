package com.lc.nlp4han.dependency;

import java.util.ArrayList;
import java.util.List;


/**
 * 依存句法分析树
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencyTree {

	private DependencySample sample;
	
	public DependencyTree() {
	}
	
	public DependencyTree(DependencySample sample) {
		this.sample = sample;
	}

	public void setTree(DependencySample sample){
		this.sample = sample;
	}
	
	public DependencySample getSample(){
		return this.sample;
	}
	
	/**
	 * 获得实根词
	 * @return
	 */
	public String getRoot(){
		int record = -1;
		String[] sentence = sample.getWords();
		String[] depedencyIndice = sample.getDependencyIndices();
		for (int i = 0; i < depedencyIndice.length; i++) {
			if(depedencyIndice[i].compareTo("0") == 0){
				record = i;
				break;
			}
		}
		return sentence[record+1];
	}
	
	/**
	 * 获得给定词的依存词
	 * @param word 词语
	 * @return
	 */
	public String getDependencyWord(String word){
		String[] words = sample.getWords();
		String[] indice = sample.getDependencyIndices();
		
		int record = -1;
		for (int i = 1; i < words.length; i++) {
			if(words[i].compareTo(word) == 0){
				record = i;
				break;
			}
		}
		int index = Integer.parseInt(indice[record-1]);
		return words[index];
	}
	
	/**
	 * 获得被当前词依存的
	 * @param word 词语
	 * @return
	 */
	public String[] getDependencyWords(String word){
		String[] words = sample.getWords();
		String[] indice = sample.getDependencyIndices();
		List<String> temp = new ArrayList<>();
		int record = -1;
		for (int i = 1; i < words.length; i++) {
			if(words[i].compareTo(word) == 0){
				record = i;
				break;
			}
		}
		
		for (int i = 0; i < indice.length; i++) {
			if(Integer.parseInt(indice[i]) == record){
				temp.add(words[i+1]);
			}
		}
		return temp.toArray(new String[temp.size()]);
	}

	@Override
	public String toString() {
		String[] words = sample.getWords();
		String[] pos = sample.getPos();
		String[] dependency = sample.getDependency();
		String[] dependencyIndice = sample.getDependencyIndices();
		int length = dependency.length;
		String output = "";
		for (int i = 0; i < length; i++) {
			output += (i+1)+"\t"+words[i+1]+"\t"+words[i+1]+"\t"
					+pos[i+1]+"\t"+pos[i+1]+"\t"
					+"_"+"\t"
					+dependencyIndice[i]+"\t"
					+dependency[i]+"\t"
					+"_"+"\t"+"_";
		}
		return output;
	}
	
	
}
