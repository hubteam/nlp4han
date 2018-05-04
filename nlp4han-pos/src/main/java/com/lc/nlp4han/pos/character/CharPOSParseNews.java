package com.lc.nlp4han.pos.character;

import java.util.ArrayList;

/**
 * 解析新闻199801词性标记的语料
 * 
 * @author 王馨苇
 * 
 */
public class CharPOSParseNews implements CharPOSSampleParser {

	/**
	 * 解析语料
	 * 
	 * @return WordSegPosSample对象
	 */
	public CharPOSSample parse(String sampleSentence) {
		String[] wordsAndPoses = sampleSentence.split("\\s+");

		ArrayList<String> characters = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> tagsAndposes = new ArrayList<String>();

		for (int i = 1; i < wordsAndPoses.length; i++) {
			String[] wordanspos = wordsAndPoses[i].split("/");
			String word = wordanspos[0];
			String pos = wordanspos[1];
			// 针对[中共中央/nt 政治局/n]nt的解析
			if (word.startsWith("[")) {
				word = word.substring(1);
				words.add(word);
			} else {
				words.add(word);
			}

			if (pos.indexOf("]") != -1) {
				int index = pos.indexOf("]");
				pos = pos.substring(0, index);
			}

			if (word.length() == 1) {
				characters.add(word + "_S");
				tagsAndposes.add("S_" + pos);
				continue;
			}
			for (int j = 0; j < word.length(); j++) {
				char c = word.charAt(j);
				if (j == 0) {
					characters.add(c + "_B");
					tagsAndposes.add("B_" + pos);
				} else if (j == word.length() - 1) {
					characters.add(c + "_E");
					tagsAndposes.add("E_" + pos);
				} else {
					characters.add(c + "_M");
					tagsAndposes.add("M_" + pos);
				}
			}
		}

		return new CharPOSSample(characters, words, tagsAndposes);
	}

}
