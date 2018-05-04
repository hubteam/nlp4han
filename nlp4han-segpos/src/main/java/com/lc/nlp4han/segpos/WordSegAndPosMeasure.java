package com.lc.nlp4han.segpos;

import java.util.HashMap;
import java.util.List;

/**
 * 计算指标
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class WordSegAndPosMeasure {

    /**
     * |selected| = true positives + false positives <br>
     * 预测的样本数
     */
    private long selected;

    /**
     * |target| = true positives + false negatives <br>
     * 参考的样本数
     */
    private long target;

    /**
     * 预测正确的个数
     */
    private long truePosPositive;
    
    private long trueSegPositiveIV;
    private long trueSegPositiveOOV;
    
    private long truePosPositiveIV;
    private long truePosPositiveOOV;
    
    private long targetIV;
    private long targetOOV;
    
    private long trueSegPositive;
    
    //句子的个数
    private long nSentences;
    
    //正确的词性标注句子
    private long nSentecesOKPos;
    
    //正确的分词的句子
    private long nSentecesOKSeg;
    
    private HashMap<String,List<String>> dictionary;
    
    public WordSegAndPosMeasure(){
        
    }
    
    /**
     * 有参构造
     * @param dictionary 训练语料得到的词典
     */
    public WordSegAndPosMeasure(HashMap<String,List<String>> dictionary){
        this.dictionary = dictionary;
    }
    
    /**
     * 更新计算指标的一些变量【词性标注和分词的指标】
     * @param wordsRef 参考的分词结果
     * @param posesRef 参考的词性标记
     * @param wordsPre 预测的分词结果
     * @param posesPre 预测的词性标记
     */
    public void updateSegAndPos(String[] wordsRef, String[] posesRef, String[] wordsPre, String[] posesPre) {
        //定义变量记录当前扫描的总长度
        int countRef = 0,countPre = 0;
        //用于统计一句中正确的词性标记或者分词的个数
        int countPos = 0;
        int countSeg = 0;
        //记录当前所在的词的位置
        int i = 0,j = 0;
        //记录i的前一次的值
        int iPre = -1;
        if(wordsRef.length > 0 && wordsPre.length > 0){
            
            while(wordsRef[i] != null || !("".equals(wordsRef[i]))|| wordsPre[j] != null || !("".equals(wordsPre[j]))){
                boolean isIV = true;
                if(iPre != i){
                    //先判断是否为登陆词未登录词,
                    if(dictionary != null){
                        //isIV = dictionary.get(wordsRef[i]);
                        if(dictionary.get(wordsRef[i]) != null){
                            isIV = true;
                            targetIV++;
                        }
                        else{
                            targetOOV++;
                            isIV = false;
                        }
                            
                    }
                }
                countRef += wordsRef[i].length();
                countPre += wordsPre[j].length();
                //匹配的情况
                if((wordsRef[i] == wordsPre[j] || wordsRef[i].equals(wordsPre[j]))){
                    //分词正确的时候
                    countSeg++;
                    trueSegPositive++;
                    if(posesRef[i] == posesPre[j] || posesRef[i].equals(posesPre[j])){
                        //词性标记正确的时候
                        countPos++;
                        truePosPositive++;     
                        
                        if(dictionary!=null){
                            if(isIV)
                                truePosPositiveIV++;
                            else
                                truePosPositiveOOV++;
                        }
                    } 
                    
                    //正确分词，且在词典中匹配了的登陆词和未登陆词
                    if(dictionary!=null){
                        if(isIV)
                            trueSegPositiveIV++;
                        else
                            trueSegPositiveOOV++;
                    }
                    iPre = i;
                    //两个字符串同时向后扫描
                    i++;j++;
                    //为了防止：已经到达边界了，还用references[i]或者predictions[i]来判断，此时越界了
                    if(i >= wordsRef.length || j >= wordsPre.length)
                        break;
   
                }else{
                    //不匹配的情况，则需要比较当前扫过的总长度
                    //（1）：长度长的那个不动，长度短的那个要继续向前扫描比较              
                    if(countRef > countPre){
                        iPre = i;
                        j++;
                        countRef -= wordsRef[i].length();
                        if(j >= wordsPre.length)
                            break;
                        //（2）：长度相等的时候，二者都需要向前扫描
                    }else if(countRef == countPre){
                        iPre = i;
                        i++;j++;
                        if(i >= wordsRef.length || j >= wordsPre.length)
                            break;
                        //（1）：长度长的那个不动，长度短的那个要继续向前扫描比较
                    }else if(countRef < countPre){
                        iPre = i;
                        i++;
                        countPre -= wordsPre[j].length();
                        if(i >= wordsRef.length)
                            break;
                    }
                }
            }
        }
        //句子的个数+1
        nSentences++;
        //判断一整个句子的全部分词是否正确
        //如果整个句子的分词都是正确的，那么wordsRef与wordsPre的长度肯定是一样的
        if(countSeg == wordsRef.length && wordsRef.length == wordsPre.length){
            nSentecesOKSeg++;
        }
        if(countPos == posesPre.length && posesRef.length == posesPre.length){
            nSentecesOKPos++;
        }
        target += posesRef.length;
        selected += posesPre.length;    
        //return truePositives;
    }
    
    /**
     * 更新计算指标的一些变量【分词的指标】
     * @param wordsRef 参考的分词结果
     * @param posesRef 参考的词性标记
     * @param wordsPre 预测的分词结果
     * @param posesPre 预测的词性标记
     */
    public void updateTag(String[] wordsRef, String[] wordsPre) {
        //定义变量记录当前扫描的总长度
        int countRef = 0,countPre = 0;
        //用于统计一句中正确的分词的个数
        int countSeg = 0;
        //记录当前所在的词的位置
        int i = 0,j = 0;
        //记录i的前一次的值
        int iPre = -1;
        if(wordsRef.length > 0 && wordsPre.length > 0){
            while(wordsRef[i] != null || !("".equals(wordsRef[i]))|| wordsPre[j] != null || !("".equals(wordsPre[j]))){
                boolean isIV = true;
                if(iPre != i){
                    //先判断是否为登陆词未登录词,
                    if(dictionary != null){
                        //isIV = dictionary.get(wordsRef[i]);
                        if(dictionary.get(wordsRef[i]) != null){
                            isIV = true;
                            targetIV++;
                        }
                        else{
                            targetOOV++;
                            isIV = false;
                        }
                            
                    }
                }
                countRef += wordsRef[i].length();
                countPre += wordsPre[j].length();
                //匹配的情况
                if((wordsRef[i] == wordsPre[j] || wordsRef[i].equals(wordsPre[j]))){
                    truePosPositive++;
                    countSeg++;
                    //正确分词，且在词典中匹配了的登陆词和未登陆词
                    if(dictionary!=null){
                        if(isIV)
                            trueSegPositiveIV++;
                        else
                            trueSegPositiveOOV++;
                    }
                    iPre = i;
                    //两个字符串同时向后扫描
                    i++;j++;
                    //为了防止：已经到达边界了，还用references[i]或者predictions[i]来判断，此时越界了
                    if(i >= wordsRef.length || j >= wordsPre.length)
                        break;
   
                }else{
                    //不匹配的情况，则需要比较当前扫过的总长度
                    //（1）：长度长的那个不动，长度短的那个要继续向前扫描比较              
                    if(countRef > countPre){
                        iPre = i;
                        j++;
                        countRef -= wordsRef[i].length();
                        if(j >= wordsPre.length)
                            break;
                        //（2）：长度相等的时候，二者都需要向前扫描
                    }else if(countRef == countPre){
                        iPre = i;
                        i++;j++;
                        if(i >= wordsRef.length || j >= wordsPre.length)
                            break;
                        //（1）：长度长的那个不动，长度短的那个要继续向前扫描比较
                    }else if(countRef < countPre){
                        iPre = i;
                        i++;
                        countPre -= wordsPre[j].length();
                        if(i >= wordsRef.length)
                            break;
                    }
                }
            }
        }
        nSentences++;
        if(countSeg == wordsRef.length && wordsRef.length == wordsPre.length){
            nSentecesOKSeg++;
        }
        target += wordsRef.length;
        selected += wordsPre.length;    
        //return truePositives;
    }
    
    
    /**
     * 词性标注准确率
     * @return
     */
    public double getPrecisionPosScore() {
        return selected > 0 ? (double) truePosPositive / (double) selected : 0;
    }

    /**
     * 词性标注召回率
     * @return
     */
    public double getRecallPosScore() { 
        return target > 0 ? (double) truePosPositive / (double) target : 0;
    }
    
    /**
     * 分词准确率
     * @return
     */
    public double getPrecisionSegScore() {
        return selected > 0 ? (double) trueSegPositive / (double) selected : 0;
    }

    /**
     * 分词召回率
     * @return
     */
    public double getRecallSegScore() { 
        return target > 0 ? (double) trueSegPositive / (double) target : 0;
    }
    
    /**
     * 词性标注F值
     * @return
     */
    public double getPosFMeasure() {

        if (getPrecisionPosScore() + getRecallPosScore() > 0) {
            return 2 * (getPrecisionPosScore() * getRecallPosScore())
                    / (getPrecisionPosScore() + getRecallPosScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }
    
    /**
     * 分词F值
     * @return
     */
    public double getSegFMeasure() {

        if (getPrecisionSegScore() + getRecallSegScore() > 0) {
            return 2 * (getPrecisionSegScore() * getRecallSegScore())
                    / (getPrecisionSegScore() + getRecallSegScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }
    
    /**
     * 登录词的分词召回率
     * @return
     */
    public double getRecallSegScoreIV(){
        return targetIV > 0 ? (double)trueSegPositiveIV / (double)targetIV : 0;
    }
    
    /**
     * 未登录词的分词召回率
     * @return
     */
    public double getRecallSegScoreOOV(){
        return targetOOV > 0 ? (double)trueSegPositiveOOV / (double)targetOOV : 0;
    }
    
    /**
     * 登录词的词性标注召回率
     * @return
     */
    public double getRecallPosScoreIV(){
        return targetIV > 0 ? (double)truePosPositiveIV / (double)targetIV : 0;
    }
    
    /**
     * 未登录词的词性标注召回率
     * @return
     */
    public double getRecallPosScoreOOV(){
        return targetOOV > 0 ? (double)truePosPositiveOOV / (double)targetOOV : 0;
    }
    
    public HashMap<String,List<String>> getDictionary(){
        return this.dictionary;
    }
    
    /**
     * 统计分词句子正确率SA
     * @return
     */
    public double getSegSA(){
        return nSentences > 0 ? (double)nSentecesOKSeg / (double)nSentences : 0;
    }
    
    /**
     * 统计词性标注句子正确率SA
     * @return
     */
    public double getPosSA(){
        return nSentences > 0 ? (double)nSentecesOKPos / (double)nSentences : 0;
    }
    
    /**
     * 打印的格式
     */
    @Override
    public String toString() {
        return "POS-Precision: " + Double.toString(getPrecisionPosScore()) + "\n"
                + "POS-Recall: " + Double.toString(getRecallPosScore()) + "\n" 
                + "POS-FMeasure: "+ Double.toString(getPosFMeasure()) + "\n"
                + "POS_SA:" + Double.toString(getPosSA()) + "\n" 
                + "POS-RIV: " + Double.toString(getRecallPosScoreIV()) + "\n"
                + "POS-ROOV: " + Double.toString(getRecallPosScoreOOV()) + "\n"
                + "SEG-Precision: " + Double.toString(getPrecisionSegScore()) + "\n"
                + "SEG-Recall: " + Double.toString(getRecallSegScore()) + "\n" 
                + "SEG-FMeasure: "+ Double.toString(getSegFMeasure()) + "\n"
                + "SEG-SA:" + Double.toString(getSegSA()) + "\n"
                + "SEG-RIV: " + Double.toString(getRecallSegScoreIV()) + "\n"
                + "SEG-ROOV: " + Double.toString(getRecallSegScoreOOV());
    }

}
