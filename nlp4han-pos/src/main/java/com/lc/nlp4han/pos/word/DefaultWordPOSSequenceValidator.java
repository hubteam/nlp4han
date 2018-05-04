package com.lc.nlp4han.pos.word;

import com.lc.nlp4han.ml.util.SequenceValidator;


public class DefaultWordPOSSequenceValidator implements SequenceValidator<String>
{
    public boolean validSequence(int i, String[] inputSequence, String[] outcomesSequence, String outcome)
    {
        return true;
    }
}
