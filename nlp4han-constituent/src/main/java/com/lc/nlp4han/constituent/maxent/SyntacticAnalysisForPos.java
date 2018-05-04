package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;


/**
 * 词性标注器
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisForPos<T extends TreeNode> {
	
	/**
	 * 得到词性标注子树序列
	 * @param words 分词数组
	 * @return
	 */
	List<T> posTree(String[] words);
	
	/**
	 * 得到词性标注子树序列
	 * @param sentence 分词句子
	 * @return
	 */
	List<T> posTree(String sentence);
	
	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 最好的K个结果
	 * @param words 分词数组
	 * @return
	 */
	List<List<T>> posTree(int k, String[] words);
	
	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 最好的K个结果
	 * @param sentece 分词句子
	 * @return
	 */
	List<List<T>> posTree(int k, String sentence);
}
