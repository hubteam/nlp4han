package com.lc.nlp4han.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * 字典装载器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DictionaryLoader
{
	/**
	 * 从字典文件中读取词集
	 * 
	 * 每行一个词
	 * 
	 * @param dictPath 字典文件路径
	 * @param encoding 字典文件编码
	 * @return 单词集合
	 * @throws IOException
	 */
	public static Set<String> getWords(String dictPath, String encoding) throws IOException
	{
		Set<String> wordSet = new HashSet<>();

		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(dictPath)), encoding));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			line = line.replaceAll("\\s", "").replaceAll("\n", "");
			if (line.length() != 0)
			{
				wordSet.add(line);
			}
		}

		br.close();

		return wordSet;
	}

	/**
	 * 从流中读取词集
	 * 
	 * 每行一个词
	 * 
	 * @param dictPath 字典流
	 * @param encoding 字典流编码
	 * @return 单词集合
	 * @throws IOException
	 */
	public static Set<String> getWords(InputStream dictStream, String encoding) throws IOException
	{
		Set<String> wordSet = new HashSet<>();

		BufferedReader br = new BufferedReader(new InputStreamReader(dictStream, encoding));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			line = line.replaceAll("\\s", "").replaceAll("\n", "");
			if (line.length() != 0)
			{
				wordSet.add(line);
			}
		}

		br.close();

		return wordSet;
	}
}
