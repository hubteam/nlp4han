package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * buildAndCheck步的序列
 * 说明：序列中的每一个元素都是该类的对象，包含下述所有信息
 * @author 王馨苇
 *
 * @param <T> 树中节点的类型
 */
public class SyntacticAnalysisSequenceForBuildAndCheck<T extends TreeNode> implements Comparable<SyntacticAnalysisSequenceForBuildAndCheck<T>> {
	private double score;
	private double scorecheck;
	private List<Double> probs;
	private List<Double> probscheck;
	private List<T> tree;
	private int begin;

	public SyntacticAnalysisSequenceForBuildAndCheck() {
		this.probs = new ArrayList<>(1);
		this.probscheck = new ArrayList<>(1);
		this.tree = new ArrayList<>(1);
		this.score = 0.0D;
		this.scorecheck = 0.0D;
		this.begin = 0;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(List<T> tree){
		this.probs = new ArrayList<>(1);
		this.probscheck = new ArrayList<>(1);
		this.tree = tree;
		this.score = 0.0D;
		this.scorecheck = 0.0D;
		this.begin = 0;
	}
	
	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck<T> s) {
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probscheck = new ArrayList<>(s.probscheck.size() + 1);
		this.probscheck.addAll(s.probscheck);
		this.score = s.score;
		this.scorecheck = s.scorecheck;
		this.tree = s.tree;
		this.begin = s.begin;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck<T> s, double p, double checkp) {
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probs.add(Double.valueOf(p));
		this.probscheck = new ArrayList<>(s.probscheck.size() + 1);
		this.probscheck.addAll(s.probscheck);
		this.probscheck.add(Double.valueOf(checkp));
		this.score = s.score + Math.log(p) + Math.log(checkp);
		this.tree = s.tree;
		this.begin = s.begin;
	}

	public SyntacticAnalysisSequenceForBuildAndCheck(SyntacticAnalysisSequenceForBuildAndCheck<T> s, List<T> tree, double p, double pcheck, int begin) {
		this.tree = tree;
		this.probs = new ArrayList<>(s.probs.size() + 1);
		this.probs.addAll(s.probs);
		this.probs.add(Double.valueOf(p));
		this.probscheck = new ArrayList<>(s.probscheck.size() + 1);
		this.probscheck.addAll(s.probscheck);
		this.probscheck.add(Double.valueOf(pcheck));
		this.score = s.score + Math.log(p) + Math.log(pcheck);
		this.begin = begin;
	}

	public int compareTo(SyntacticAnalysisSequenceForBuildAndCheck<T> s) {
		return this.score < s.score ? 1 : (this.score > s.score ? -1 : 0);
	}

	public void add(double p, double pcheck) {
		this.probs.add(Double.valueOf(p));
		this.probscheck.add(Double.valueOf(pcheck));
		this.score += Math.log(p) + Math.log(pcheck);
	}
	
	public double[] getProbs() {
		double[] ps = new double[this.probs.size()];
		this.getProbs(ps);
		return ps;
	}

	public double[] getProbsCheck() {
		double[] ps = new double[this.probscheck.size()];
		this.getProbsCheck(ps);
		return ps;
	}
	
	public double getScore() {
		return this.score;
	}
	
	public List<T> getTree(){
		return this.tree;
	}
	
	public int getBegin(){
		return this.begin;
	}
	
	public void getProbs(double[] ps) {
		int pi = 0;
		for (int pl = this.probs.size(); pi < pl; ++pi) {
			ps[pi] = ((Double) this.probs.get(pi)).doubleValue();
		}
	}

	public void getProbsCheck(double[] ps){
		int pi = 0;
		for (int pl = this.probscheck.size(); pi < pl; ++pi) {
			ps[pi] = ((Double) this.probscheck.get(pi)).doubleValue();
		}
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < this.tree.size(); i++) {
			str += this.tree.get(i).toString();
		}
		return str;
	}
}