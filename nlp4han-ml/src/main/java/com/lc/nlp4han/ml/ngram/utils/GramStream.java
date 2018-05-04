package com.lc.nlp4han.ml.ngram.utils;

import java.io.IOException;

/**
 *<ul>
 *<li>Description: 从流中读取元的接口  
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月17日
 *</ul>
 */
public interface GramStream {
	
	/**
	 * <li>返回下一个元。
	 * <li>重复调用该方法每次将从底层源中返回一个元，直到返回null。  
	 * @return 下一个元或者null（读取结束）
	 * @throws IOException
	 */
	Gram next() throws IOException;

	/**
	 * <li>在开始处重置流，之前读取的元序列将会完全重新读取。 
	 * <li>该方法的实现是可选的。
	 *
	 * @throws IOException 如果重置流的过程中出错抛出异常
	 */
	void reset() throws IOException, UnsupportedOperationException;

	/**
	 * <li>关闭GramStream并释放所有资源。
	 * <li>调用close后，将无法再调用read和reset。
	 * @throws IOException 如果关闭流的过程中出错抛出异常
	 */
	void close() throws IOException;
}
