package com.lc.nlp4han.ml.hmm.utils;

import java.io.Serializable;
import java.util.List;

/**
 * <ul>
 *<li>Description: 观测序列，状态序列接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月27日
 *</ul>
 * @param <T>序列元素的类型
 */
public interface Sequence<T> extends Serializable {
    
	public Sequence<T> addFirst(T token);
	
    public Sequence<T> addLast(T token);
    
    public Sequence<T> addLast(T[] tokens);
    
    public Sequence<T> addLast(Sequence<T> sequence);
    
    public Sequence<T> set(T token, int index);
    
    public Sequence<T> remove(int index);
    
    public T get(int index);
    
    public List<T> asList();
    
    public T[] toArray();
    
    public int length();
    
    @Override
	public int hashCode();

	@Override
	public boolean equals(Object object);
	
	@Override
    public String toString();
}
