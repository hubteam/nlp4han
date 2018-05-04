package com.lc.nlp4han.chunk.wordpos;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisParse;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureWithBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureWithBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureWithBIO;
import com.lc.nlp4han.chunk.word.ChunkAnalysisSequenceValidatorWithBIEO;
import com.lc.nlp4han.chunk.word.ChunkAnalysisSequenceValidatorWithBIEOS;
import com.lc.nlp4han.chunk.word.ChunkAnalysisSequenceValidatorWithBIO;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * <ul>
 * <li>Description: 交叉验证工具类
 * <li>Company: HUST
 * <li>@author Sonly
 * <li>Date: 2017年12月18日
 * </ul>
 */
public class ChunkAnalysisWordPosCrossValidatorTool {

	private static void usage() {
		System.out.println(ChunkAnalysisWordPosCrossValidatorTool.class.getName()
				+ " -data <corpusFile> -type <type> -label <label> -encoding <encoding> [-folds <nFolds>] [-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		if (args.length < 1) {
			usage();
			return;
		}

		int cutoff = 3;
		int iters = 100;
		int folds = 10;
		// Maxent, Perceptron, MaxentQn, NaiveBayes
		String type = "Maxent";
		String label = "BIEO";
		File corpusFile = null;
		String encoding = "UTF-8";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-data")) {
				corpusFile = new File(args[i + 1]);
				i++;
			} else if (args[i].equals("-type")) {
				type = args[i + 1];
				i++;
			} else if (args[i].equals("-label")) {
				label = args[i + 1];
				i++;
			} else if (args[i].equals("-encoding")) {
				encoding = args[i + 1];
				i++;
			} else if (args[i].equals("-cutoff")) {
				cutoff = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("-iters")) {
				iters = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("-folds")) {
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());

		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile),
				encoding);
		AbstractChunkAnalysisParse parse = null;
		AbstractChunkAnalysisMeasure measure = null;
		SequenceValidator<String> sequenceValidator = null;

		if (label.equals("BIEOS")) {
			parse = new ChunkAnalysisWordPosParseWithBIEOS();
			measure = new ChunkAnalysisMeasureWithBIEOS();
			sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEOS();
		} else if (label.equals("BIEO")) {
			parse = new ChunkAnalysisWordPosParseWithBIEO();
			measure = new ChunkAnalysisMeasureWithBIEO();
			sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEO();
		} else {
			parse = new ChunkAnalysisWordPosParseWithBIO();
			measure = new ChunkAnalysisMeasureWithBIO();
			sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIO();
		}

		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf();
		ChunkAnalysisWordPosCrossValidation crossValidator = new ChunkAnalysisWordPosCrossValidation(
				params);
		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisWordPosSampleStream(
				lineStream, parse, label);

		crossValidator.evaluate(sampleStream, folds, contextGen, measure, sequenceValidator);

	}
}
