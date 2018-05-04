package com.lc.nlp4han.segpos;

import java.util.ArrayList;

/**
 * 解析新闻199801词性标记的语料
 * @author 王馨苇
 *
 */
public class WordSegAndPosParseNews implements WordSegAndPosParseStrategy{

	/**
	 * 解析语料
	 * @return WordSegPosSample对象
	 */
	public WordSegAndPosSample parse(String sampleSentence) {
		String[] wordsAndPoses = sampleSentence.split("\\s+");
		
		ArrayList<String> characters = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
	    ArrayList<String> tags = new ArrayList<String>();
	    ArrayList<String> poses = new ArrayList<String>();
	    
	    for (int i = 0; i < wordsAndPoses.length; i++) {
	    	String[] wordanspos = wordsAndPoses[i].split("/");
	    	String word = wordanspos[0];
	    	String pos = wordanspos[1];
	    	//针对[中共中央/nt  政治局/n]nt的解析
	    	if(word.startsWith("[")){
	    		word = word.substring(1);
	    		words.add(word);
	    	}else{
	    		words.add(word);
	    	}
	    	
	    	if(pos.indexOf("]") != -1){
	    		int index = pos.indexOf("]");
	    		poses.add(pos.substring(0, index));
	    	}else{
	    		poses.add(pos);
	    	}
	    	
	    	if(word.length() == 1){
	    		characters.add(word);
		    	tags.add("S");
		    	continue;
	    	}
	    	for (int j = 0; j < word.length(); j++) {
				char c = word.charAt(j);
			    if (j == 0) {
					characters.add(c + "");
	                tags.add("B");
	            } else if (j == word.length() - 1) {
	                characters.add(c + "");
	                tags.add("E");
	            } else {
	                characters.add(c + "");
	                tags.add("M");
	            }	
			}
		}
		return new WordSegAndPosSample(characters,tags,words,poses);
	}

}
