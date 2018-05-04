package com.lc.nlp4han.constituent.maxent;

import java.io.IOException;
import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.segpos.WordSegAndPosContextGenerator;
import com.lc.nlp4han.segpos.WordSegAndPosContextGeneratorConf;
import com.lc.nlp4han.segpos.WordSegAndPosME;

/**
 * 中文的词性标注器
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForPosChinese implements SyntacticAnalysisForPos<HeadTreeNode>{

	private WordSegAndPosME postagger ;
	private WordSegAndPosContextGenerator generator ;
	
	public SyntacticAnalysisMEForPosChinese(ModelWrapper posmodel) throws IOException {
		generator = new WordSegAndPosContextGeneratorConf();
		postagger = new WordSegAndPosME(posmodel, generator);
	}
	
	/**
	 * 得到词性标注子树序列
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String[] words) {
		String[][] poses = postagger.tag(1, words);
		List<List<HeadTreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, poses);
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
		String[][] poses = postagger.tag(k, words);
		List<List<HeadTreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, poses);
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
