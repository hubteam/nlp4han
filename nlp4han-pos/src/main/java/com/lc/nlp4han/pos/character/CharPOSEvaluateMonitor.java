package com.lc.nlp4han.pos.character;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 评估监控器
 * 
 * @author 王馨苇
 * 
 */
public class CharPOSEvaluateMonitor implements EvaluationMonitor<CharPOSSample>
{

    /**
     * 预测正确的时候执行
     * 
     * @param arg0
     *            参考的结果
     * @param arg1
     *            预测的结果
     */
    public void correctlyClassified(CharPOSSample arg0, CharPOSSample arg1)
    {

    }

    /**
     * 预测正确的时候执行
     * 
     * @param arg0
     *            参考的结果
     * @param arg1
     *            预测的结果
     */
    public void missclassified(CharPOSSample arg0, CharPOSSample arg1)
    {

    }

}
