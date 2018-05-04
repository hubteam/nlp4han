package com.lc.nlp4han.ml.hmm.io;

import java.io.IOException;

/**
 *<ul>
 *<li>Description: 模型读入接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月14日
 *</ul>
 */
public interface DataReader {
	
	/**
	 * 读入整型数据 
	 * @return 整型数据 
	 * @throws IOException
	 */
	
	public int readCount() throws IOException;
	
	/**
	 * 读入观测状态索引
	 * @return	观测状态索引
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public ObservationIndex readObservationIndex() throws IOException, ClassNotFoundException;
	
	/**
	 * 读入隐藏状态索引
	 * @return	隐藏状态索引
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public StateIndex readStateIndex() throws IOException, ClassNotFoundException;
	
	/**
	 * 读入初始转移向量
	 * @return	初始转移向量的条目
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public PiEntry readPi() throws IOException, ClassNotFoundException;
	
	/**
	 * 读入转移矩阵
	 * @return	转移矩阵的条目
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public TransitionEntry readTransitionMatrix() throws IOException, ClassNotFoundException;
	
	/**
	 * 读入发射矩阵
	 * @return 发射矩阵的条目
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public EmissionEntry readEmissionMatrix() throws IOException, ClassNotFoundException;

	/**
	 * 关闭流  
	 * @throws IOException
	 */
	
	public void close() throws IOException;
}
