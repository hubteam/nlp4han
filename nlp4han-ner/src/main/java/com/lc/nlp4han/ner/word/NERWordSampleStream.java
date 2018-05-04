package com.lc.nlp4han.ner.word;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 为基于分词的命名实体识别过滤文本流得到想要的样式
 * @author 王馨苇
 *
 */
public class NERWordSampleStream extends FilterObjectStream<String,NERWordOrCharacterSample>{

	private static Logger logger = Logger.getLogger(NERWordSampleStream.class.getName());
	private NERParseStrategy parse;
	
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public NERWordSampleStream(ObjectStream<String> samples, NERParseStrategy parse) {
		super(samples);
		
		this.parse = parse;
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
					sample = parse.parse(sentence);;
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
