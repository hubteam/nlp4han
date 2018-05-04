package com.lc.nlp4han.ml.ngram.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lc.nlp4han.ml.ngram.model.AbstractNGramCountWriter;
import com.lc.nlp4han.ml.ngram.model.NGramCounter;
import com.lc.nlp4han.ml.ngram.utils.NGramCountEntry;


/**
 *<ul>
 *<li>Description: 将n元计数器以二进制形式写入文件 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public class BinaryFileNGramCountWriter extends AbstractNGramCountWriter{
	
	private DataOutputStream dos;

	public BinaryFileNGramCountWriter(NGramCounter counter, File file) throws IOException {
		super(counter);
		
		dos = new DataOutputStream(new FileOutputStream(file));
	}

	public BinaryFileNGramCountWriter(NGramCounter counter, DataOutputStream dos) throws IOException {
		super(counter);
		
		this.dos = dos;
	}
	
	@Override
	public void writeCountEntry(NGramCountEntry entry) throws IOException {
		dos.writeUTF(entry.toString());
	}
	
	@Override
	public void writeNumber(int number) throws IOException {
		dos.writeInt(number);
	}

	@Override
	public void close() throws IOException {
		dos.flush();
		dos.close();
	}
}
