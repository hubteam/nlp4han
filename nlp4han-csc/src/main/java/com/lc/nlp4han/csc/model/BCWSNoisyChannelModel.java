package com.lc.nlp4han.csc.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.lc.nlp4han.csc.ngram.NGramModel;
import com.lc.nlp4han.csc.util.ConfusionSet;
import com.lc.nlp4han.csc.util.Sentence;
import com.lc.nlp4han.csc.wordseg.AbstractWordSegment;

/**
 *<ul>
 *<li>Description: 基于中文分词的检测方法（BCWS-> Based on Chinese Word Segmentation），并利用ngram模型计算句子得分
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年10月18日
 *</ul>
 */
public class BCWSNoisyChannelModel extends AbstractNoisyChannelModel {
	
	private AbstractWordSegment wordSegment;
	
	public BCWSNoisyChannelModel(NGramModel nGramModel, ConfusionSet confusionSet, AbstractWordSegment wordSegment) throws IOException {
		super(confusionSet, nGramModel);
		
		this.wordSegment = wordSegment;
	}
	
	public BCWSNoisyChannelModel(NGramModel nGramModel, ConfusionSet confusionSet, AbstractWordSegment wordSegment, double magicNumber) throws IOException {
		super(confusionSet, nGramModel, magicNumber);
		
		this.wordSegment = wordSegment;
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
		
		//连续单字词的最大个数小于2，不作处理直接返回原句
		if(locations.size() > 1) {
			//连续单字词的个数最大等于2的使用bigram，大于2的使用trigram
			int maxLength = maxContinueSingleWordsLength(locations);
			if(maxLength <= 2) 
				order = 2;
			else
				order = 3;
			
			candSens = beamSearch(confusionSet, beamSize, sentence, locations);
			return candSens;
		}
		
		candSens.add(sentence);
		return candSens;
	}

	@Override
	public double getChannelModelLogScore(Sentence sentence, int location, String candidate, HashSet<String> cands) {
		return 1.0;
	}

	@Override
	public double getSourceModelLogScore(Sentence candidate) {
		return nGramModel.getSentenceLogProb(candidate, order);
	}
}