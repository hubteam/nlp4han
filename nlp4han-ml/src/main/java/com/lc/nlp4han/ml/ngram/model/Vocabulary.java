package com.lc.nlp4han.ml.ngram.model;

import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.ml.ngram.utils.Gram;
import com.lc.nlp4han.ml.ngram.utils.GramSentenceStream;
import com.lc.nlp4han.ml.ngram.utils.GramStream;
import com.lc.nlp4han.ml.ngram.utils.PseudoWord;


/**
 *<ul>
 *<li>Description: 字典类，用户给定字典文件建立字典类，用于判断训练语料中的未登录词(oov) 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年9月5日
 *</ul>
 */
public class Vocabulary {
	
	private HashSet<Gram> vocabulary;
		
	public Vocabulary() {
		this.vocabulary = new HashSet<>();
		add(PseudoWord.oov);
	}
	
	public Vocabulary(GramStream vocab) throws IOException {
		this.vocabulary = new HashSet<>();
		establishVocab(vocab);
		add(PseudoWord.oov);
	}
	
	public Vocabulary(GramSentenceStream vocab) throws IOException {
		this.vocabulary = new HashSet<>();
		establishVocab(vocab);
		add(PseudoWord.oov);
	}
	
	public Vocabulary(Gram[] vocab, boolean isSentence) {
		this.vocabulary = new HashSet<>();
		establishVocab(vocab);
		add(PseudoWord.oov);
	}
	
	public boolean isSentence(){
		return contains(PseudoWord.Start) && contains(PseudoWord.End) ? true :false;
	}
	
	/**
	 * 添加元到字典中
	 * @param gram 待添加的元
	 */
	public void add(Gram gram) {
		if(!vocabulary.contains(gram))
			vocabulary.add(gram);
	}
	
	/**
	 * 字典的大小
	 * @return 字典的大小
	 */
	public int size() {
		return isSentence() ? vocabulary.size() - 1 : vocabulary.size();
	}

	/**
	 * 判断元是否在字典中
	 * @param gram
	 * @return 在-true/不在-false
	 */
	public boolean contains(Gram gram) {
		return vocabulary.contains(gram);
	}
	
	/**
	 * 建立字典
	 * @param stream	建立字典的语料
	 * @throws IOException
	 */
	private void establishVocab(GramStream stream) throws IOException {
		Gram gram = null;
		while((gram = stream.next()) != null) {
			if(!vocabulary.contains(gram))
				add(gram);
		}
	}
	
	/**
	 * 建立字典
	 * @param stream	建立字典的语料
	 * @throws IOException
	 */
	private void establishVocab(GramSentenceStream stream) throws IOException {
		Gram[] grams = null;
		while((grams = stream.nextSentence()) != null) {
			for(Gram gram : grams)
				if(!vocabulary.contains(gram))
					add(gram);
		}
		add(PseudoWord.End);
		add(PseudoWord.Start);
	}

	/**
	 * 建立字典
	 * @param grams	建立字典的语料
	 */
	private void establishVocab(Gram[] grams) {
		for(Gram gram: grams) {
			if(!vocabulary.contains(gram))
				add(gram);
		}
	}
}
