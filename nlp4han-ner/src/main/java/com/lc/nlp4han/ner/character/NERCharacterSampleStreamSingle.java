package com.lc.nlp4han.ner.character;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ner.word.NERParseStrategy;
import com.lc.nlp4han.ner.word.NERWordOrCharacterSample;


public class NERCharacterSampleStreamSingle extends FilterObjectStream<String,NERWordOrCharacterSample>{

	private static Logger logger = Logger.getLogger(NERCharacterSampleStreamSingle.class.getName());
	
	private NERParseStrategy sampleParse;
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public NERCharacterSampleStreamSingle(ObjectStream<String> samples, NERParseStrategy sampleParse) {
		super(samples);
		
		this.sampleParse = sampleParse;
	}

	/**
	 * 读取样本进行解析
	 * @return 
	 */	
	public NERWordOrCharacterSample read() throws IOException {
		String sentence = samples.read();
		NERWordOrCharacterSample sample = null;
		if(sentence != null){
			if(sentence.compareTo("") != 0){
				try{
					sample = sampleParse.parse(sentence);;
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING)) {
						
	                    logger.warning("Error during parsing, ignoring sentence: " + sentence);
	                }
					sample = new NERWordOrCharacterSample(new String[]{},new String[]{});
				}
				return sample;
			}else {
				sample = new NERWordOrCharacterSample(new String[]{},new String[]{});
				return sample;
			}
		}
		else{
			return null;
		}
	}
}
