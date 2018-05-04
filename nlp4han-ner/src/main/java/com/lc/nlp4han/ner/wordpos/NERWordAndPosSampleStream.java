package com.lc.nlp4han.ner.wordpos;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ner.word.NERParseStrategy;
import com.lc.nlp4han.ner.word.NERWordOrCharacterSample;

/**
 * 为基于词性标注的命名实体识别过滤文本流得到想要的样式
 * @author 王馨苇
 *
 */
public class NERWordAndPosSampleStream extends FilterObjectStream<String,NERWordOrCharacterSample>{

	private static Logger logger = Logger.getLogger(NERWordAndPosSampleStream.class.getName());
	
	private NERParseStrategy sampleParse;
	
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public NERWordAndPosSampleStream(ObjectStream<String> samples, NERParseStrategy sampleParse) {
		super(samples);
		
		this.sampleParse = sampleParse;
	}

	/**
	 * 读取样本进行解析
	 * @return 
	 */	
	public NERWordOrCharacterSample read() throws IOException {
		String sentence = samples.read();
		NERWordAndPosSample sample = null;
		if(sentence != null){
			if(sentence.compareTo("") != 0){
				try{
					sample = (NERWordAndPosSample) sampleParse.parse(sentence);;
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING)) {					
	                    logger.warning("Error during parsing, ignoring sentence: " + sentence);
	                }
					sample = new NERWordAndPosSample(new String[]{},new String[]{},new String[]{});
				}
				return sample;
			}else {
				sample = new NERWordAndPosSample(new String[]{},new String[]{},new String[]{});
				return sample;
			}
		}
		else{
			return null;
		}
	}
}
