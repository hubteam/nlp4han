package com.lc.nlp4han.ner.wordpos;

import com.lc.nlp4han.ner.NamedEntity;

/**
 * 基于词性标注的命名实体识别
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public interface NERWordAndPos
{

	/**
	 * 读入一句词性标注的语料，得到最终结果
	 * 
	 * @param sentence
	 *            读取的词性标注的语料
	 * @return
	 */
	public NamedEntity[] ner(String sentence);

	/**
	 * 读入词性标注的语料，得到命名实体
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @return
	 */
	public NamedEntity[] ner(String[] words, String[] poses);

	/**
	 * 读入一句词性标注的语料，得到指定的命名实体
	 * 
	 * @param sentence
	 *            读取的词性标注的语料，词和词性间/分割
	 * @param flag
	 *            命名实体标记
	 * @return
	 */
	public NamedEntity[] ner(String sentence, String flag);

	/**
	 * 读入词性标注的语料，得到指定的命名实体
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @param flag
	 *            命名实体标记
	 * @return
	 */
	public NamedEntity[] ner(String[] words, String[] poses, String flag);

	/**
	 * 读入一句词性标注的语料，得到最好的K个结果
	 * 
	 * @param sentence
	 *            读取的词性标注的语料，词和词性间/分割
	 * @return
	 */
	public NamedEntity[][] ner(String sentence, int k);

	/**
	 * 读入词性标注的语料，得到最好的K个命名实体
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @return
	 */
	public NamedEntity[][] ner(String[] words, String[] poses, int k);
}
