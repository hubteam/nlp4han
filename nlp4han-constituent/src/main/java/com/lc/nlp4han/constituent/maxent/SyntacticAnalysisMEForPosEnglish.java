package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.pos.word.POSTaggerWordME;

/**
 * 英文的词性标注器
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForPosEnglish implements SyntacticAnalysisForPos<HeadTreeNode> {

	private POSTaggerWordME postagger ;
	
	public SyntacticAnalysisMEForPosEnglish(ModelWrapper posmodel) {
		postagger = new POSTaggerWordME(posmodel);
	}

	/**
	 * 得到词性标注子树序列
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String[] words) {
		String[][] posres = postagger.tag(words,1);
		List<List<HeadTreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, posres);
		return posTree.get(0);
	}

	/**
	 * 得到词性标注子树序列
	 * @param sentence 分词句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String sentence) {
		String[] words = sentence.split(" ");
		return posTree(words);
	}

	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 最好的K个结果
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public List<List<HeadTreeNode>> posTree(int k, String[] words) {
		String[][] posres = postagger.tag(words, k);
		List<List<HeadTreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, posres);
		return posTree;
	}

	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 最好的K个结果
	 * @param sentece 分词句子
	 * @return
	 */
	@Override
	public List<List<HeadTreeNode>> posTree(int k, String sentence) {
		String[] words = sentence.split(" ");
		return posTree(k, words);
	}
}
