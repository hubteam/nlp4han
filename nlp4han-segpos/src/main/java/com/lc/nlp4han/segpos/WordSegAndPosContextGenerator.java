package com.lc.nlp4han.segpos;

import com.lc.nlp4han.ml.util.BeamSearchContextGenerator;

/**
 * 上下文特征生成器
 * @author 王馨苇
 *
 */
public interface WordSegAndPosContextGenerator extends BeamSearchContextGenerator<String>{

	/**
	 * 词性标注的训练语料生成特征
	 * @param i 当前字符的位置
	 * @param j 当前字符属于词语的位置
	 * @param characters 字符的序列
	 * @param tags 字符的标记序列
	 * @param words 词语序列
	 * @param poses 词性序列
	 * @param ac 额外的信息
	 * @return
	 */
	public String[] getContext(int i, int j, String[] characters, String[] tags, String[] words, String[] poses, Object[] ac);
	
	/**
	 * 没有分词的测试语料生成特征
	 * @param i 当前字符的位置
	 * @param characters 字符的序列
	 * @param tags 字符的标记序列
	 */
	public String[] getContext(int i, String[] characters, String[] tags, Object[] ac);
}
