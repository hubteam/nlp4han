package com.lc.nlp4han.dependency;

/**
 * 依存样本解析成内部表示
 * 
 * @author 刘小峰
 *
 */
public interface DependencySampleParser{

	/**
	 * 从训练语料中读取的要解析的句子
	 * 
	 * @param sentence 要解析的语句
	 * @return 解析的结果
	 */
	public DependencySample parse(String sentence);

}
