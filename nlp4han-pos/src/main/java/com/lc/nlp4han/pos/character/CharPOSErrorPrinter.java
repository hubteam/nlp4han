package com.lc.nlp4han.pos.character;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 打印错误信息类
 * 
 * @author 刘小峰
 * @author 王馨苇
 * 
 */
public class CharPOSErrorPrinter extends CharPOSEvaluateMonitor
{

    private PrintStream errOut;

    public CharPOSErrorPrinter(OutputStream out)
    {
        errOut = new PrintStream(out);
    }

    /**
     * 样本和预测的不一样的时候进行输出
     * 
     * @param reference
     *            参考的样本
     * @param predict
     *            预测的结果
     */
    @Override
    public void missclassified(CharPOSSample reference, CharPOSSample predict)
    {
        errOut.print(reference.toSample());
        errOut.println();
        errOut.print("[*]" + predict.toSample());
        errOut.println();
    }

}
