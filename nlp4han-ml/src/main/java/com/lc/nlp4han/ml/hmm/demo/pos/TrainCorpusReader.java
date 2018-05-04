package com.lc.nlp4han.ml.hmm.demo.pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.stream.UnSupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.StringObservation;
import com.lc.nlp4han.ml.hmm.utils.StringState;

/**
 *<ul>
 *<li>Description: 训练样本读取
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年3月27日
 *</ul>
 */
public class TrainCorpusReader {

	public static List<SupervisedHMMSample> readSupervisedHMMSamples(File file, int order) throws IOException {
		if(order < 1)
			throw new IllegalArgumentException("模型阶数必须为正整数");
		
		List<SupervisedHMMSample> samples = new ArrayList<>();
		InputStreamReader ireader = new InputStreamReader(new FileInputStream(file), "utf8");
		BufferedReader reader = new BufferedReader(ireader);
		
		String line = null;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(!line.equals("")) {
				String[] wordTags = line.split("\\s+");
				if(wordTags.length <= order) {
					System.err.println("句子太短，抛弃: " + line);
					continue;
				}
				SupervisedHMMSample sample = new SupervisedHMMSample();
				
				for(int i = 0; i < wordTags.length; i++)
					sample.add(new StringState(wordTags[i].split("/")[1]), new StringObservation(wordTags[i].split("/")[0]));
				
				samples.add(sample);
			}
		}
		reader.close();
		
		return samples;
	}
	
	public static List<UnSupervisedHMMSample> readUnSupervisedHMMSamples(File file) throws IOException {
		List<UnSupervisedHMMSample> samples = new ArrayList<>();
		InputStreamReader ireader = new InputStreamReader(new FileInputStream(file), "utf8");
		BufferedReader reader = new BufferedReader(ireader);
		
		String line = null;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(!line.equals("")) {
				String[] wordTags = line.split("\\s+");
				if(wordTags.length <2)
					System.err.println("句子太短，抛弃: " + line);
				UnSupervisedHMMSample sample = new UnSupervisedHMMSample();
				
				for(int i = 0; i < wordTags.length; i++)
					sample.add(new StringObservation(wordTags[i].split("/")[0]));
				
				samples.add(sample);
			}
		}
		reader.close();
		
		return samples;
	}
}
