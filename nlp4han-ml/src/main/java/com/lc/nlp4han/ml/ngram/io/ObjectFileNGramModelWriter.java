package com.lc.nlp4han.ml.ngram.io;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.lc.nlp4han.ml.ngram.model.AbstractNGramModelWriter;
import com.lc.nlp4han.ml.ngram.model.NGramLanguageModel;
import com.lc.nlp4han.ml.ngram.utils.NGramModelEntry;


/**
 *<ul>
 *<li>Description: 以序列化的形式将模型写入文件  
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ObjectFileNGramModelWriter extends AbstractNGramModelWriter {
	
	ObjectOutputStream dos;
	
	public ObjectFileNGramModelWriter(NGramLanguageModel languageModel, ObjectOutputStream dos) {
		super(languageModel);
		this.dos = dos;
	}

	@Override
	public void writeNGramModelEntry(NGramModelEntry entry) throws IOException {
		dos.writeObject(entry);
	}

	@Override
	public void writeUTF(String string) throws IOException {
		dos.writeUTF(string);
	}

	@Override
	public void writeCount(int count) throws IOException {
		dos.writeInt(count);
	}

	@Override
	public void close() throws IOException {
		dos.close();
	}
}
