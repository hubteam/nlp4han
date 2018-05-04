package com.lc.nlp4han.csc.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.lc.nlp4han.csc.ngram.NGramModel;
import com.lc.nlp4han.csc.util.ConfusionSet;
import com.lc.nlp4han.csc.util.Dictionary;
import com.lc.nlp4han.csc.util.Sentence;
import com.lc.nlp4han.csc.wordseg.AbstractWordSegment;

/**
 *<ul>
 *<li>Description: 在HUST噪音通道模型的基础上，引入字的概率,字的概率通过平衡语料获取 (pro = count/totalConfusion)
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年11月16日
 *</ul>
 */
public class HUSTNoisyChannelModelBasedCharacterInBalance extends AbstractNoisyChannelModel {
	
	private Dictionary dictionary;
	private AbstractWordSegment wordSegment;
	private Dictionary charDict;
 
	public HUSTNoisyChannelModelBasedCharacterInBalance(Dictionary dictionary, NGramModel nGramModel, ConfusionSet confusionSet,
			AbstractWordSegment wordSegment, String charType) throws IOException {
		super(confusionSet, nGramModel);
		
		this.dictionary = dictionary;
		this.wordSegment = wordSegment;
		charDict = buildCharDict(charType);
	}
	
	public HUSTNoisyChannelModelBasedCharacterInBalance(Dictionary dictionary, NGramModel nGramModel, ConfusionSet confusionSet,
			AbstractWordSegment wordSegment, String charType, double magicNumber) throws IOException {
		super(confusionSet, nGramModel, magicNumber);
		
		this.dictionary = dictionary;
		this.wordSegment = wordSegment;
		charDict = buildCharDict(charType);
	}
	
	@Override
	public Sentence getBestSentence(Sentence sentence) {
		return getBestKSentence(sentence, 1).get(0);
	}
	
	@Override
	public ArrayList<Sentence> getBestKSentence(Sentence sentence, int k) {
		if(k < 1)
			throw new IllegalArgumentException("返回候选句子数目不能小于1");
		beamSize = k;
		ArrayList<Sentence> candSens = new ArrayList<>();
		ArrayList<String> words = wordSegment.segment(sentence);
		
		if(words.size() < 2) {//分词后，词的个数小于2的不作处理，不作处理直接返回原句
			candSens.add(sentence);
			return candSens;
		}
		
		ArrayList<Integer> locations = locationsOfSingleWords(words);
		ArrayList<Integer> errorLoations = getErrorLocationsBySIMD(dictionary, sentence);
		for(int index : errorLoations) {
			if(!locations.contains(index))
				locations.add(index);
		}
		Collections.sort(locations);
		
		//连续单字词的最大个数小于2，不作处理直接返回原句
		if(locations.size() > 1) {					
			candSens = beamSearch(confusionSet, beamSize, sentence, locations);
			return candSens;
		}
		
		candSens.add(sentence);
		return candSens;
	}

	@Override
	public double getSourceModelLogScore(Sentence candidate) {
		return nGramModel.getSentenceLogProb(candidate, order);
	}

	@Override
	public double getChannelModelLogScore(Sentence sentence, int location, String candidate, HashSet<String> cands) {
		double total = getTotalCharcterCount(cands, charDict);
		int count = charDict.getCount(candidate);
		
		return count / total;
	}	
}
