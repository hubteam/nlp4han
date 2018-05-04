package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.ModelWrapper;

/**
 * 成分树分析器
 * @author 王馨苇
 *
 */
public class ConstituentParserME implements ConstituentParser{

	private SyntacticAnalysisForPos<HeadTreeNode> postagger;
	private SyntacticAnalysisMEForChunk chunktagger;
	private SyntacticAnalysisMEForBuildAndCheck  buildAndChecktagger;
	
	public ConstituentParserME(SyntacticAnalysisForPos<HeadTreeNode> postagger, ModelWrapper chunkmodel, ModelWrapper buildmodel, ModelWrapper checkmodel,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, AbstractHeadGenerator aghw) {
		this.postagger = postagger;
		this.chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen, aghw);
		this.buildAndChecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildmodel, checkmodel, contextGen, aghw);
	}
	
	/**
	 * 得到最好的成分树
	 * @param words 分词序列
	 * @param poses 词性标记
	 * @return
	 */
	@Override
	public ConstituentTree parseTree(String[] words, String[] poses) {
		String[][] kposes = new String[1][poses.length];
		for (int i = 0; i < kposes.length; i++) {
			for (int j = 0; j < kposes[i].length; j++) {
				kposes[i][j] = poses[j];
			}
		}
		
		List<List<HeadTreeNode>> postree = SyntacticAnalysisSample.toPosTree(words, kposes);
		List<HeadTreeNode> chunkTree = chunktagger.tagChunk(postree, null);
		List<List<HeadTreeNode>> kchunkTree = new ArrayList<>();
		kchunkTree.add(chunkTree);
		
		HeadTreeNode headTreeNode = buildAndChecktagger.tagBuildAndCheck(kchunkTree, null);
		
		ConstituentTree constituent = new ConstituentTree();
		constituent.setRoot(headTreeNode);
		return constituent;
	}

	/**
	 * 得到最好的成分树
	 * @param words 分词序列
	 * @return
	 */
	@Override
	public ConstituentTree parseTree(String[] words) {
		List<HeadTreeNode> postree = postagger.posTree(words);
		List<List<HeadTreeNode>> postrees = new ArrayList<>();
		postrees.add(postree);
		List<HeadTreeNode> chunkTree = chunktagger.tagChunk(postrees, null);
		List<List<HeadTreeNode>> kchunkTree = new ArrayList<>();
		kchunkTree.add(chunkTree);
		HeadTreeNode headTreeNode = buildAndChecktagger.tagBuildAndCheck(kchunkTree, null);
		ConstituentTree constituent = new ConstituentTree();
		constituent.setRoot(headTreeNode);
		return constituent;
	}

	/**
	 * 得到最好的K个成分树
	 * @param k 最好的K个结果
	 * @param words 词语
	 * @param poses 词性标记
	 * @return
	 */
	@Override
	public ConstituentTree[] parseKTree(int k, String[] words, String[] poses) {
		String[][] kposes = new String[1][poses.length];
		for (int i = 0; i < kposes.length; i++) {
			for (int j = 0; j < kposes[i].length; j++) {
				kposes[i][j] = poses[j];
			}
		}
		List<List<HeadTreeNode>> postree = SyntacticAnalysisSample.toPosTree(words, kposes);
		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(k, postree, null);
		List<HeadTreeNode> headTreeNode = buildAndChecktagger.tagBuildAndCheck(k,chunkTree, null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++) {
			ConstituentTree con = new ConstituentTree();
			con.setRoot(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}

	/**
	 * 得到最好的K个成分树
	 * @param k 最好的K个结果
	 * @param words 分词序列
	 * @return
	 */
	@Override
	public ConstituentTree[] parseKTree(int k, String[] words) {
		List<List<HeadTreeNode>> postree = postagger.posTree(k, words);
		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(k, postree, null);
		List<HeadTreeNode> headTreeNode = buildAndChecktagger.tagBuildAndCheck(k,chunkTree, null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++) {
			ConstituentTree con = new ConstituentTree();
			con.setRoot(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}
}
