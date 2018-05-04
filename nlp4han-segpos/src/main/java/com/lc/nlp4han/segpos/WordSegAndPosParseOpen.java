package com.lc.nlp4han.segpos;

import java.util.ArrayList;

/**
 * 对OpenNLP格式的词性标注语料解析
 * 
 * 词和词性间用_连接
 * 
 * @author 刘小峰
 *
 */
public class WordSegAndPosParseOpen implements WordSegAndPosParseStrategy
{
    /**
     * 解析语料
     * 
     * @return WordSegPosSample对象
     */
    public WordSegAndPosSample parse(String sampleSentence)
    {
        String[] wordsAndPoses = sampleSentence.split("\\s+");

        ArrayList<String> characters = new ArrayList<String>();
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> tags = new ArrayList<String>();
        ArrayList<String> poses = new ArrayList<String>();

        for (int i = 0; i < wordsAndPoses.length; i++)
        {
            String[] wordanspos = wordsAndPoses[i].split("_");
            String word = wordanspos[0];
            String pos = wordanspos[1];

            words.add(word);

            poses.add(pos);

            if (word.length() == 1)
            {
                characters.add(word);
                tags.add("S");
                continue;
            }
            for (int j = 0; j < word.length(); j++)
            {
                char c = word.charAt(j);
                if (j == 0)
                {
                    characters.add(c + "");
                    tags.add("B");
                }
                else if (j == word.length() - 1)
                {
                    characters.add(c + "");
                    tags.add("E");
                }
                else
                {
                    characters.add(c + "");
                    tags.add("M");
                }
            }
        }
        return new WordSegAndPosSample(characters, tags, words, poses);
    }
}
