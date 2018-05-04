package com.lc.nlp4han.ml.ngram.model;

import java.io.IOException;
import java.util.Iterator;

import com.lc.nlp4han.ml.ngram.utils.ARPAEntry;
import com.lc.nlp4han.ml.ngram.utils.GramSentenceStream;
import com.lc.nlp4han.ml.ngram.utils.GramStream;
import com.lc.nlp4han.ml.ngram.utils.NGram;

/**
 *<ul>
 *<li>Description: 最大似然n元模型训练
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年9月3日
 *</ul>
 */
public class MLLanguageModelTrainer extends AbstractLanguageModelTrainer {

	public MLLanguageModelTrainer(GramStream gramStream, int n) throws IOException {
		super(gramStream, n);
	}
	
	public MLLanguageModelTrainer(GramSentenceStream gramSentenceStream, int n) throws IOException {
		super(gramSentenceStream, n);
	}
	
	public MLLanguageModelTrainer(NGramCounter nGramCounter, int n) {
		super(nGramCounter, n);
	}

	@Override
	public NGramLanguageModel trainModel() {
		
		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			
			double prob = calcMLNGramProbability(nGram, nGramCounter);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
			nGramLogProbability.put(nGram, entry);
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "ml", vocabulary);
	}
}
