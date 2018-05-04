package com.lc.nlp4han.segpos;

/**
 * 上下文产生器
 * @author 王馨苇
 *
 * @param <T>
 */
public interface BeamSearchWordSegAndPosContextGenerator<T> {

	String[] getContext(int i, int j, T[] characters, T[] tags, T[] words, String[] poses, Object[] ac);
}
