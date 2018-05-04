package com.lc.nlp4han.ml.hmm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 *<ul>
 *<li>Description: 读取序列化数据 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月14日
 *</ul>
 */
public class ObjectDataReader implements DataReader {

	private ObjectInputStream ois;
	
	public ObjectDataReader(InputStream is) throws IOException {
		ois = new ObjectInputStream(is);
	}

	public ObjectDataReader(ObjectInputStream ois) {
		this.ois = ois;
	}

	@Override
	public int readCount() throws IOException {
		return ois.readInt();
	}

	@Override
	public ObservationIndex readObservationIndex() throws IOException, ClassNotFoundException {
		return (ObservationIndex) ois.readObject();
	}

	@Override
	public StateIndex readStateIndex() throws IOException, ClassNotFoundException {
		return (StateIndex) ois.readObject();
	}
	
	@Override
	public PiEntry readPi() throws IOException, ClassNotFoundException {
		return (PiEntry) ois.readObject();
	}

	@Override
	public TransitionEntry readTransitionMatrix() throws IOException, ClassNotFoundException {
		return (TransitionEntry) ois.readObject();
	}

	@Override
	public EmissionEntry readEmissionMatrix() throws IOException, ClassNotFoundException {
		return (EmissionEntry) ois.readObject();
	}
	
	@Override
	public void close() throws IOException {
		ois.close();
	}
}