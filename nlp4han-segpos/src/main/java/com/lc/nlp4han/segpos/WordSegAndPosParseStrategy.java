package com.lc.nlp4han.segpos;


/**
 * 解析语料接口
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public interface WordSegAndPosParseStrategy {

	/**
	 * 解析语料读取的一条语句
	 * @param sentence 要解析的句子
	 * @return WordSegPosSample格式的语料信息
	 */
	public WordSegAndPosSample parse(String sentence);
}
