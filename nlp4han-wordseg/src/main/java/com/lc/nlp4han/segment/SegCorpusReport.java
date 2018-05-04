package com.lc.nlp4han.segment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 分词语料统计报告
 * 
 * @author 刘小峰
 *
 */
public class SegCorpusReport
{
	private HashMap<String, Integer> word2Count;
	
	private HashMap<Character, Integer> char2Count;
	
	private int wordTokens;

	private int charTokens;
	
	private HashMap<Integer, HashSet<String>> count2Words = new HashMap<Integer, HashSet<String>>();
	
	private HashMap<Integer, HashSet<Character>> count2Chars = new HashMap<Integer, HashSet<Character>>();
	
	private int maxWordCount = 0;
	private int minWordCount = 0;
	
	private int maxCharCount = 0;
	private int minCharCount = 0;
	

	public SegCorpusReport(HashMap<String, Integer> word2Count,
			HashMap<Character, Integer> char2Count, int wordTokens, int charTokens)
	{
		this.word2Count = word2Count;
		this.char2Count = char2Count;
		
		this.wordTokens = wordTokens;
		this.charTokens = charTokens;
		
		for(Map.Entry<String, Integer> e : word2Count.entrySet())
		{
			String word = e.getKey();
			int count = e.getValue();
			
			if(count>maxWordCount)
				maxWordCount = count;
			
			if(count<minWordCount)
				minWordCount = count;
			
			HashSet<String> words = new HashSet<String>();
			if(count2Words.containsKey(count))
				words = count2Words.get(count);
			words.add(word);
		}
		
		for(Map.Entry<Character, Integer> e : char2Count.entrySet())
		{
			char c = e.getKey();
			int count = e.getValue();
			
			if(count>maxCharCount)
				maxCharCount = count;
			
			if(count<minCharCount)
				minCharCount = count;
			
			HashSet<Character> chars = new HashSet<Character>();
			if(count2Chars.containsKey(count))
				chars = count2Chars.get(count);
			chars.add(c);
		}
	}

	/**
	 * 词条数
	 * @return 词条数
	 */
	public int numWordTokens()
	{
		return wordTokens;
	}
	
	/**
	 * 词形数
	 * 
	 * @return 词形数
	 */
	public int numWordTypes()
	{
		return word2Count.keySet().size();
	}
	
	/**
	 * 字数
	 * 
	 * @return 字数
	 */
	public int numCharTokens()
	{
		return charTokens;
	}
	
	/**
	 * 字形数
	 * @return 字形数
	 */
	public int numCharTypes()
	{
		return char2Count.keySet().size();
	}
	
	/**
	 * 词典
	 * @return 词典
	 */
	public Set<String> words()
	{
		return word2Count.keySet();
	}
	
	/**
	 * 字典
	 * @return 字典
	 */
	public Set<Character> chars()
	{
		return char2Count.keySet();
	}
	
	/**
	 * 给定词出现的次数
	 * @param word
	 * @return 给定词出现的次数
	 */
	public int getWordOccurences(String word)
	{
		if(!word2Count.containsKey(word))
			return 0;
		else
			return word2Count.get(word);
	}
	
	/**
	 * 给定字出现的次数
	 * @param c
	 * @return
	 */
	public int getCharOccurences(char c)
	{
		if(!char2Count.containsKey(c))
			return 0;
		else
			return char2Count.get(c);
	}
	
	/**
	 * 出现最多的词的出现次数
	 * @return 出现最多的词的出现次数
	 */
	public int getMaxWordOccurences()
	{
		return maxWordCount;
	}
	
	/**
	 * 出现最多的字的出现次数
	 * @return 出现最多的字的出现次数
	 */
	public int getMinWordOccurences()
	{
		return minWordCount;
	}
	
	/**
	 * 出现最少的词的出现次数
	 * @return 出现最少的词的出现次数
	 */
	public int getMaxCharOccurences()
	{
		return maxCharCount;
	}
	
	/**
	 * 出现最少的字的出现次数
	 * @return 出现最少的字的出现次数
	 */
	public int getMinCharOccurences()
	{
		return minCharCount;
	}
	
	/**
	 * 出现给定次数的词的列表
	 * @param n
	 * @return 出现给定次数的词的列表
	 */
	public Set<String> getWords(int n)
	{
		if(!count2Words.containsKey(n))
			return new HashSet<String>();
		else
			return count2Words.get(n);
	}
	
	/**
	 * 出现给定次数的字的列表
	 * @param n
	 * @return 出现给定次数的字的列表
	 */
	public Set<Character> getChars(int n)
	{
		if(!count2Chars.containsKey(n))
			return new HashSet<Character>();
		else
			return count2Chars.get(n);
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		
		buf.append("词条数：" + numWordTokens() + "\n");
		buf.append("词形数：" + numWordTypes() + "\n");
		buf.append("字条数：" + numCharTokens() + "\n");
		buf.append("字形数：" + numCharTypes() + "\n");
		
		return buf.toString();
	}
	
	
}
