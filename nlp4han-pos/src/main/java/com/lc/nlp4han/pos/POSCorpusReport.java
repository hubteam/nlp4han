package com.lc.nlp4han.pos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 词性标注语料统计报告
 * 
 * @author 刘小峰
 *
 */
public class POSCorpusReport
{
	private HashMap<String, HashSet<String>> word2Tags;
	private HashMap<String, Integer> tag2Count;
	
	private HashMap<String, HashSet<String>> tag2Words;
	
	private int nWordTokens;
	
	public POSCorpusReport(HashMap<String, HashSet<String>> word2Tags, HashMap<String, Integer> tag2Count,
			HashMap<String, HashSet<String>> tag2Words,
			int nWordTokens)
	{
		this.word2Tags = word2Tags;
		this.tag2Count = tag2Count;
		this.nWordTokens = nWordTokens;
		this.tag2Words = tag2Words;
	}

	/**
	 * 词条数
	 * @return 词条数
	 */
	public int numWordTokens()
	{
		return nWordTokens;
	}
	
	/**
	 * 词形数
	 * 
	 * @return 词形数
	 */
	public int numWordTypes()
	{
		return word2Tags.keySet().size();
	}
	
	/**
	 * 词典
	 * @return 词典
	 */
	public Set<String> words()
	{
		return word2Tags.keySet();
	}
	
	/**
	 * 词性集
	 * @return 词性集
	 */
	public Set<String> tags()
	{
		return tag2Count.keySet();
	}
	
	/**
	 * 给定词性的所有词
	 * @param tag 词性
	 * @return
	 */
	public Set<String> words(String tag)
	{
		if(tag2Words.containsKey(tag))
			return tag2Words.get(tag);
		else
			return new HashSet<String>();
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
        buf.append("词条数: " + nWordTokens + "\n");
        buf.append("词形数: " + word2Tags.size() + "\n");
        buf.append("词性数: " + tag2Count.size() + "\n");
        
        return buf.toString();
	}
	
}
