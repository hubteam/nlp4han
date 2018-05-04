package com.lc.nlp4han.ml.hmm.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lc.nlp4han.ml.hmm.model.HMModel;

/**
 *<ul>
 *<li>Description: 将模型写入二进制文件
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月14日
 *</ul>
 */
public class BinaryFileHMMWriter extends AbstractHMMWriter {

	private DataOutputStream dos;
	
	public BinaryFileHMMWriter(HMModel model, String pathname) throws IOException {
		this(model, new File(pathname));
	}
	
	public BinaryFileHMMWriter(HMModel model, File file) throws IOException {
		super(model);
		dos = new DataOutputStream(new FileOutputStream(file));
	}
	
	public BinaryFileHMMWriter(HMModel model, DataOutputStream dos) throws IOException {
		super(model);
		this.dos = dos;
	}

	@Override
	public void writeCount(int count) throws IOException {
		dos.writeInt(count);
	}

	@Override
	public void writeStateIndex(StateIndex entry) throws IOException {
		dos.writeUTF(entry.toString());
	}
	
	@Override
	public void writeObservationIndex(ObservationIndex entry) throws IOException {
		dos.writeUTF(entry.toString());
	}

	@Override
	public void writePi(PiEntry entry) throws IOException {
		dos.writeUTF(entry.toString());
	}

	@Override
	public void writeTransitionMatrix(TransitionEntry entry) throws IOException {
		dos.writeUTF(entry.toString());
	}

	@Override
	public void writeEmissionMatrix(EmissionEntry entry) throws IOException {
		dos.writeUTF(entry.toString());
	}
	
	@Override
	public void close() throws IOException {
		dos.flush();
		dos.close();
	}
}
