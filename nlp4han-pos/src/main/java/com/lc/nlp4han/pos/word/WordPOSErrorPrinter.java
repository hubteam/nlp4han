package com.lc.nlp4han.pos.word;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 词性标注评价中输出错误的结果，帮助进行结果分析
 * 
 * @author 刘小峰
 */
public class WordPOSErrorPrinter implements WordPOSTaggerEvaluationMonitor
{

    private PrintStream errOut;

    public WordPOSErrorPrinter(OutputStream out)
    {
        errOut = new PrintStream(out);
    }

    @Override
    public void missclassified(WordPOSSample reference, WordPOSSample prediction)
    {
        errOut.println(reference.toString());
        errOut.println("[*]" + prediction.toString());
    }

    @Override
    public void correctlyClassified(WordPOSSample reference, WordPOSSample prediction)
    {     
    }
}
