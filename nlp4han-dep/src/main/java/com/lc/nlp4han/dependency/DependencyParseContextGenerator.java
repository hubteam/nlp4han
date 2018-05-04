package com.lc.nlp4han.dependency;

/**
 * 特征生成的接口
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public interface DependencyParseContextGenerator{

	/**
	 * 获取特征
	 * 
	 * @param indexi 词语1的位置
	 * @param indexj 词语2的位置
	 * @param words 句子词序列
	 * @param pos 词性序列
	 * @param ac 额外的信息
	 * @return
	 */
	 public String[] getContext(int indexi, int indexj, String[] words, String[] pos, Object[] ac);
}
