package com.lc.nlp4han.dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析Stanford样式的依存分析语料
 * 
 * @author 王馨苇
 *
 */
public class DependencySampleParserStanford implements DependencySampleParser{

	@Override
	public DependencySample parse(String sentence) {
		String wordLine[] = sentence.split("\\n");
		//词
		List<String> word = new ArrayList<String>();
		//依赖关系
		List<String> dependency = new ArrayList<String>();
		//依赖词语的下标
		List<String> dependencyIndices = new ArrayList<String>();
		//依赖的词语
		List<String> dependencyWords = new ArrayList<String>();
		
		word.add("核心");
		for (int i = 0; i < wordLine.length; i++) {
			word.add("");
			dependency.add("");
			dependencyIndices.add("");
			dependencyWords.add("");
		}
		
		for (int i = 0; i < wordLine.length; i++) {
			String[] temp = wordLine[i].split("\\(");
				
			String[] str = temp[1].split("\\,");
			
			String wordindex = str[1].split("-")[1].substring(0, str[1].split("-")[1].length() - 1);
			int index = Integer.parseInt(wordindex);
			System.out.println(str[1].split("-")[0]);
			word.set(index, str[1].split("-")[0]);
			dependency.set(index - 1, temp[0]);
			dependencyWords.set(index - 1, str[0].split("-")[0]);
			dependencyIndices.set(index - 1, str[0].split("-")[1]);
		}

		return new DependencySample(word.toArray(new String[word.size()]), 
				null, 
				dependency.toArray(new String[dependency.size()]), 
				dependencyWords.toArray(new String[dependencyWords.size()]), 
				dependencyIndices.toArray(new String[dependencyIndices.size()]));
	}
}
