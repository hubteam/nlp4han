package com.lc.nlp4han.ner.character;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ner.word.NERParseStrategy;
import com.lc.nlp4han.ner.word.NERWordOrCharacterSample;

/**
 * 为基于字的命名实体识别过滤文本流得到想要的样式
 * @author 王馨苇
 *
 */
public class NERCharacterSampleStream extends FilterObjectStream<String,NERWordOrCharacterSample>{

	//自定义日志记录器
	private static Logger logger = Logger.getLogger(NERCharacterSampleStream.class.getName());
	
	private NERParseStrategy sampleParse;
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public NERCharacterSampleStream(ObjectStream<String> samples, NERParseStrategy sampleParse) {
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
					//System.out.println(sentences);
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