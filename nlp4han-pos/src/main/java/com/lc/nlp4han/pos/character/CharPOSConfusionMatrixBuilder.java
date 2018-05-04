package com.lc.nlp4han.pos.character;


import com.lc.nlp4han.pos.ConfusionMatrix;

public class CharPOSConfusionMatrixBuilder extends CharPOSEvaluateMonitor
{

    private ConfusionMatrix matrix = new ConfusionMatrix();

    @Override
    public void missclassified(CharPOSSample reference, CharPOSSample prediction)
    {
        updateMatrix(reference, prediction);
    }

    @Override
    public void correctlyClassified(CharPOSSample reference, CharPOSSample prediction)
    { 
        updateMatrix(reference, prediction);
    }
    
    private void updateMatrix(CharPOSSample reference, CharPOSSample prediction)
    {
        String[] refTags = reference.getPoses();
        String[] preTags = prediction.getPoses();
        
        matrix.add(refTags, preTags);
    }
    
    public ConfusionMatrix getMatrix()
    {
        return matrix;
    }

}
