package com.lc.nlp4han.segment;

/**
 * 概率中文分词器
 * 
 * 可以按照概率从大到小返回多个切分结果
 * 
 * @author 刘小峰
 *
 */
public interface WordSegmenterProb extends WordSegmenter
{
    /**
     * 对句子进行切分，并返回至多k个概率最高的切分序列
     * 
     * 返回多个序列按照概率非升序排列
     *
     * @param sentence 待切分的句子
     * @param k 至多k个
     * @return 至多k个概率最高的切分序列
     */
    public String[][] segment(String sentence, int k);
}
