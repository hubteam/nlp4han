package com.lc.nlp4han.pos.hmm;

import com.lc.nlp4han.pos.WordPOSMeasure;

/**
 * 模型评估接口。
 */
public interface ModelEval {

    /**
     * 模型评估的执行方法
     */
    void eval() throws Exception;

    /**
     * 返回模型的评分
     */
    WordPOSMeasure getScores();

}
