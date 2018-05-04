package com.lc.nlp4han.dependency;

import java.util.ArrayList;
import java.util.List;


/**
 * 对CoNLL格式的依存语料进行解析
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencySampleParserCoNLL implements DependencySampleParser{

	
	/**
	 * 解析样本
	 * @param 待解析的语句
	 * @return 解析后的样本
	 */
	public DependencySample parse(String sentence) {
		String wordLine[] = sentence.split("\\n");
		//词
		List<String> word = new ArrayList<String>();
		//词性
		List<String> pos = new ArrayList<String>();
		//依赖关系
		List<String> dependency = new ArrayList<String>();
		//依赖词语的下标
		List<String> dependencyIndices = new ArrayList<String>();
		//依赖的词语
		List<String> dependencyWords = new ArrayList<String>();
		
		word.add("核心");
		pos.add("root");
		for (int i = 0; i < wordLine.length; i++) {
			String[] temp = wordLine[i].split("\\t");
			word.add(temp[1]);
			pos.add(temp[3]);
			dependencyIndices.add(temp[6]);
			dependency.add(temp[7]);		
		}
		for (int i = 0; i < dependencyIndices.size(); i++) {
			String tempword = word.get(Integer.parseInt(dependencyIndices.get(i)));
			dependencyWords.add(tempword);
		}

		return new DependencySample(word.toArray(new String[word.size()]), 
				pos.toArray(new String[pos.size()]), 
				dependency.toArray(new String[dependency.size()]), 
				dependencyWords.toArray(new String[dependencyWords.size()]), 
				dependencyIndices.toArray(new String[dependencyIndices.size()]));
	}

}
