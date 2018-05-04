package com.lc.nlp4han.pos.word;


import com.lc.nlp4han.pos.ConfusionMatrix;


/**
 * 根据词性标注结果生成词性标注混淆矩阵
 * 
 * @author 刘小峰
 *
 */
public class WordPOSConfusionMatrixBuilder implements WordPOSTaggerEvaluationMonitor
{
    private ConfusionMatrix matrix = new ConfusionMatrix();

    @Override
    public void missclassified(WordPOSSample reference, WordPOSSample prediction)
    {
        updateMatrix(reference, prediction);
    }

    @Override
    public void correctlyClassified(WordPOSSample reference, WordPOSSample prediction)
    { 
        updateMatrix(reference, prediction);
    }
    
    private void updateMatrix(WordPOSSample reference, WordPOSSample prediction)
    {
        String[] refTags = reference.getTags();
        String[] preTags = prediction.getTags();
        
        matrix.add(refTags, preTags);
    }
    
    public ConfusionMatrix getMatrix()
    {
        return matrix;
    }
}
