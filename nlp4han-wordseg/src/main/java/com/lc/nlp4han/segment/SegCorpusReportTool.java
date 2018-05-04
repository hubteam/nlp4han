package com.lc.nlp4han.segment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 统计中文分词语料基本情况应用
 * 
 * @author 刘小峰
 *
 */
public class SegCorpusReportTool
{

	private static void usage()
	{
		System.out.println(SegCorpusReportTool.class.getName() + " -data <corpusFile> [-encoding encoding]");
	}

	public static SegCorpusReport report(String source, String encoding) throws IOException
	{
		System.out.println("语料统计数据 for " + source);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
		int wordTokens = 0;
		int charTokens = 0;
		HashMap<String, Integer> word2Count = new HashMap<String, Integer>();
		HashMap<Character, Integer> char2Count = new HashMap<Character, Integer>();

		String sentence = null;
		while ((sentence = in.readLine()) != null)
		{
			String[] words = sentence.split("\\s+");
			
			wordTokens += words.length;
			
			for(String word : words)
			{
				charTokens += word.length();
				
				int count = 0;
				if(word2Count.containsKey(word))
				{
					count = word2Count.get(word);
				}
				
				count++;
				word2Count.put(word, count);
				
				for(int i=0; i<word.length(); i++)
				{
					char c = word.charAt(i);
					
					count = 0;
					if(char2Count.containsKey(c))
					{
						count = char2Count.get(c);
					}
					
					count++;
					char2Count.put(c, count);
				}
			}
		}
		
		in.close();
		
		return new SegCorpusReport(word2Count, char2Count, wordTokens, charTokens);
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			usage();
			return;
		}

		String corpusFile = null;
		String encoding = "GBK";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}

		SegCorpusReport report = report(corpusFile, encoding);

		System.out.println(report);
	}

}
