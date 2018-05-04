package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chunk步的序列
 * 说明：序列中的每一个元素都是该类的对象，包含下述所有信息
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSequenceForChunk implements Comparable<SyntacticAnalysisSequenceForChunk> {
	private double score;
	private List<String> outcomes;
	private List<Double> probs;
	private int lable;//标记是输入K个结果中的第几个
	private static final Double ONE = Double.valueOf(1.0D);

	public SyntacticAnalysisSequenceForChunk(int lable) {
		this.outcomes = new ArrayList<>(1);
		this.probs = new ArrayList<>(1);
		this.score = 0.0D;
		this.lable = lable;
	}

	public SyntacticAnalysisSequenceForChunk(SyntacticAnalysisSequenceForChunk s) {
		this.outcomes = new ArrayList<>(s.outcomes.size() + 1);
		this.outcomes.addAll(s.outcomes);
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.score = s.score;
		this.lable = s.lable;
	}

	public SyntacticAnalysisSequenceForChunk(SyntacticAnalysisSequenceForChunk s, String outcome, double p, int lable) {
		this.outcomes = new ArrayList<>(s.outcomes.size() + 1);
		this.outcomes.addAll(s.outcomes);
		this.outcomes.add(outcome);
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probs.add(Double.valueOf(p));
		this.score = s.score + Math.log(p);
		this.lable = lable;
	}

	public SyntacticAnalysisSequenceForChunk(List<String> outcomes, int lable) {
		this.outcomes = outcomes;
		this.probs = Collections.nCopies(outcomes.size(), ONE);
		this.lable = lable;
	}

	public int compareTo(SyntacticAnalysisSequenceForChunk s) {
		return this.score < s.score ? 1 : (this.score > s.score ? -1 : 0);
	}

	public void add(String outcome, double p) {
		this.outcomes.add(outcome);
		this.probs.add(Double.valueOf(p));
		this.score += Math.log(p);
	}

	public List<String> getOutcomes() {
		return this.outcomes;
	}

	public double[] getProbs() {
		double[] ps = new double[this.probs.size()];
		this.getProbs(ps);
		return ps;
	}

	public double getScore() {
		return this.score;
	}

	public int getLabel(){
		return this.lable;
	}
	
	public void getProbs(double[] ps) {
		int pi = 0;

		for (int pl = this.probs.size(); pi < pl; ++pi) {
			ps[pi] = ((Double) this.probs.get(pi)).doubleValue();
		}

	}

	public String toString() {
		return this.score + " " + this.outcomes+" "+this.lable;
	}
}