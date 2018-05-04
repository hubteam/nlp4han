package com.lc.nlp4han.pos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 词性标注语料统计报告应用
 * 
 * @author 刘小峰
 *
 */
public class POSCorpusReportTool
{
	private static void usage()
	{
		System.out.println(POSCorpusReportTool.class.getName() + " -data <corpusFile> [-encoding encoding]");
	}
	
	public static POSCorpusReport report(String source, String encoding) throws IOException
    {
        System.out.println("语料统计数据 for " + source);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
        String sentence = null;
        int nWordTokens = 0;
        HashMap<String, HashSet<String>> word2Tags = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> tag2Count = new HashMap<String, Integer>();
        
        HashMap<String, HashSet<String>> tag2Words = new HashMap<String, HashSet<String>>();
        
        String wordTagSep = "_";

        while ((sentence = in.readLine()) != null)
        {
            if (sentence.length() == 0)
                continue;

            String[] wordtags = sentence.split("\\s+");

            for (int i = 0; i < wordtags.length; i++)
            {
                String wordtag = wordtags[i];

                int pos = wordtag.lastIndexOf(wordTagSep);

                if (pos < 0)
                    continue;

                String word = wordtag.substring(0, pos);
                String tag = wordtag.substring(pos + 1);

                nWordTokens++;

                if (!word2Tags.containsKey(word))
                {
                    HashSet<String> tags = new HashSet<String>();
                    tags.add(tag);

                    word2Tags.put(word, tags);
                }
                else
                {
                    HashSet<String> tags = word2Tags.get(word);
                    tags.add(tag);

                    word2Tags.put(word, tags);
                }

                HashSet<String> words;
                if (!tag2Count.containsKey(tag))
                {
                    tag2Count.put(tag, 1);
                    
                     words = new HashSet<String>();
                }
                else
                {
                    int n = tag2Count.get(tag);
                    n++;

                    tag2Count.put(tag, n);
                    
                    words = tag2Words.get(tag);
                }
                
                words.add(word);
                tag2Words.put(tag, words);
            }

        }

        in.close();
        
        return new POSCorpusReport(word2Tags, tag2Count, tag2Words, nWordTokens);

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

		POSCorpusReport report = report(corpusFile, encoding);

		System.out.println(report);
	}
}
