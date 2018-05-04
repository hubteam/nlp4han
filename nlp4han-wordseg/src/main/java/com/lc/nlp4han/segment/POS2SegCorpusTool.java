package com.lc.nlp4han.segment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 将中文词性标注语料转换成分析语料应用
 * 
 * @author 刘小峰
 *
 */
public class POS2SegCorpusTool
{
	private static void usage()
	{
		System.out.println(POS2SegCorpusTool.class.getName() + " -data <posFile> -output <outputFile> [-format pd|opennlp] [-encoding encoding]");
	}
	
	public static void convert(String posFile, String outputFile, String format, String encoding) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(posFile), encoding));
        String sentence = null;
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
        while ((sentence = in.readLine()) != null)
        {
        	String[] words = null;
        	if(format.equals("opennlp"))
        		words = convertOpenNLP(sentence);
        	else
        		words = convertPD(sentence);
        	
        	for(int i=0; i<words.length; i++)
        	{
        		if(i<words.length-1)
        			out.append(words[i] + " ");
        		else
        			out.append(words[i]);
        	}
        	
        	out.append("\n");
        }
        
        out.close();
        in.close();
	}
	
	private static String[] convertPD(String line)
	{
		String[] wordtags = line.split("\\s+");

		String[] words = new String[wordtags.length];
        for (int i = 0; i < wordtags.length; i++)
        {
        	String wordtag = wordtags[i];

            int index = wordtag.lastIndexOf("[");
            if (index >= 0)
                wordtag = wordtag.substring(index + 1);

            index = wordtag.lastIndexOf("]");
            if (index >= 0)
                wordtag = wordtag.substring(0, index);

            index = wordtag.lastIndexOf("/");
            if (index < 0)
                words[i] = wordtag;
            else
            	words[i] = wordtag.substring(0, index);
        }
        
        return words;
	}

	private static String[] convertOpenNLP(String line)
	{
		String[] wordtags = line.split("\\s+");

		String[] words = new String[wordtags.length];
        for (int i = 0; i < wordtags.length; i++)
        {
        	int index = wordtags[i].lastIndexOf("_");
        	if(index>=0)
        		words[i] = wordtags[i].substring(0, index);
        	else
        		words[i] = wordtags[i];
        }
        
        return words;
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			usage();
			return;
		}

		String posFile = null;
		String encoding = "GBK";
		String outputFile = null;
		String format = "opennlp";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				posFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-output"))
			{
				outputFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-format"))
			{
				format = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		
		convert(posFile, outputFile, format, encoding);
	}

}
