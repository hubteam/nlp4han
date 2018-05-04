package com.lc.nlp4han.chunk;

import com.lc.nlp4han.ml.util.BeamSearchContextGenerator;

/**
 *<ul>
 *<li>Description: 基于词的组块分析模型特征生成接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface ChunkAnalysisContextGenerator extends BeamSearchContextGenerator<String>{

	@Override
	String[] getContext(int index, String[] words, String[] chunkTags, Object[] additionalContext);
}
