package com.lc.nlp4han.pos.character;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 读取文件流，并解析成要的格式返回
 * 
 * @author 刘小峰
 * @author 王馨苇
 * 
 */
public class CharPOSSampleStream extends FilterObjectStream<String, CharPOSSample>
{

    private static Logger logger = Logger.getLogger(CharPOSSampleStream.class.getName());

    private CharPOSSampleParser contextParse;

    /**
     * 有参构造函数
     * 
     * @param samples
     *            读取的一行未解析的样本流
     * @param contextParse
     *            样本解析
     */
    public CharPOSSampleStream(ObjectStream<String> samples, CharPOSSampleParser contextParse)
    {
        super(samples);

        this.contextParse = contextParse;
    }

    /**
     * 读取一行的内容，并解析成词，词性的格式
     * 
     * @return 返回解析之后的结果
     */
    public CharPOSSample read() throws IOException
    {

        String sentence = samples.read();
        CharPOSSample sample = null;
        if (sentence != null)
        {
            if (sentence.compareTo("") != 0)
            {
                try
                {
                    sample = contextParse.parse(sentence);
                }
                catch (Exception e)
                {
                    if (logger.isLoggable(Level.WARNING))
                    {

                        logger.warning("Error during parsing, ignoring sentence: " + sentence);
                    }
                    sample = new CharPOSSample(new String[]{},new String[]{},new String[]{});
                }

                return sample;
            }
            else
            {
                sample = new CharPOSSample(new String[]{},new String[]{},new String[]{});
                return sample;
            }
        }
        else
        {
            return null;
        }

    }

}
