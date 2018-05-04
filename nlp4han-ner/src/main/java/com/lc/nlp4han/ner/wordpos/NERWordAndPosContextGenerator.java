package com.lc.nlp4han.ner.wordpos;

import com.lc.nlp4han.ml.util.BeamSearchContextGenerator;

public interface NERWordAndPosContextGenerator extends BeamSearchContextGenerator<String>{

	String[] getContext(int index,String[] words, String[] tags, Object[] ac);
	
}
