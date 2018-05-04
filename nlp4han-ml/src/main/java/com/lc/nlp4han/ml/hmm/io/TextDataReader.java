package com.lc.nlp4han.ml.hmm.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.State;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;
import com.lc.nlp4han.ml.hmm.utils.StringObservation;
import com.lc.nlp4han.ml.hmm.utils.StringState;

/**
 *<ul>
 *<li>Description: 读取普通文本数据 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2018年1月14日
 *</ul>
 */
public class TextDataReader implements DataReader {

	private BufferedReader bufferedReader;

	public TextDataReader(File file) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
	}

	public TextDataReader(InputStream in) {
		bufferedReader = new BufferedReader(new InputStreamReader(in));
	}

	public TextDataReader(BufferedReader bReader) {
		this.bufferedReader = bReader;
	}

	@Override
	public int readCount() throws IOException {
		return Integer.parseInt(bufferedReader.readLine());
	}

	@Override
	public ObservationIndex readObservationIndex() throws IOException {
		String line = bufferedReader.readLine();
		String[] items = line.split("\t");
		
		return new ObservationIndex(new StringObservation(items[0]), Integer.parseInt(items[1]));
	}
	
	@Override
	public StateIndex readStateIndex() throws IOException {
		String line = bufferedReader.readLine();
		String[] items = line.split("\t");
		
		return new StateIndex(new StringState(items[0]), Integer.parseInt(items[1]));
	}

	@Override
	public PiEntry readPi() throws IOException {
		String line = bufferedReader.readLine();
		String[] items = line.split("\t");
		
		return new PiEntry(new StringState(items[0]), Double.parseDouble(items[1]));
	}

	@Override
	public TransitionEntry readTransitionMatrix() throws IOException {
		String line = bufferedReader.readLine();
		String[] items = line.split("\t");
		
		
		String[] states = items[0].split(" ");
		State[] start = new StringState[states.length];
		for(int i = 0; i < states.length; i++)
			start[i] = new StringState(states[i]);
				
		return new TransitionEntry(new StateSequence(start), new StringState(items[1]), Double.parseDouble(items[2]));
	}

	@Override
	public EmissionEntry readEmissionMatrix() throws IOException {
		String line = bufferedReader.readLine();
		String[] items = line.split("\t");
		
		State state = new StringState(items[0]);
		Observation observation = new StringObservation(items[1]);
		double logProb = Double.parseDouble(items[2]);
		
		return new EmissionEntry(state, observation, logProb);
	}

	@Override
	public void close() throws IOException {
		bufferedReader.close();
	}
}