package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;


/**
 * 语块识别器
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisForChunk<T extends TreeNode> {

	/**
	 * 得到chunk子树
	 * @param words 词语
	 * @param poses 词性
	 * @return
	 */
	List<T> chunkTree(String[] words, String[] poses);
	/**
	 * 得到chunk子树
	 * @param wordsandposes 词语+词性组成的数组
	 * @return
	 */
	List<T> chunkTree(String[] wordsandposes);
	/**
	 * 得到chunk子树
	 * @param wordsandposes 词语+词性组成的句子
	 * @return
	 */
	List<T> chunkTree(String wordsandposes);
	/**
	 * 得到chunk子树
	 * @param posTree pos子树
	 * @return
	 */
	List<T> chunkTree(List<T> posTree);
	/**
	 * 得到chunk结果
	 * @param words 词语
	 * @param poses 词性
	 * @return
	 */
	String[] chunk(String[] words, String[] poses);
	/**
	 * 得到chunk结果
	 * @param wordsandposes 词语+词性组成数组
	 * @return
	 */
	String[] chunk(String[] wordsandposes);
	/**
	 * 得到chunk结果
	 * @param wordsandposes 词语+词性组成的句子
	 * @return
	 */
	String[] chunk(String wordsandposes);
	/**
	 * 得到chunk结果
	 * @param posTree pos子树
	 * @return
	 */
	String[] chunk(List<T> posTree);
}
