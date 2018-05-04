package com.lc.nlp4han.constituent.maxent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.SyntacticAnalysisMeasure;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 英文句法分析评估运行类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvalTool {

	private static void usage(){
		System.out.println(SyntacticAnalysisEvalTool.class.getName() + 
				"-data <corpusFile> -type <algorithom> -postagger <postagger>"
				+ "-gold <goldFile> -error <errorFile> -encoding <encoding>" + " [-cutoff <num>] [-iters <num>]");
	}
	
	public static void eval(String postaggertype, File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException{
		long start = System.currentTimeMillis();
		SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
		System.out.println(contextGen);
		AbstractHeadGenerator aghw = new HeadGeneratorCollins();
		ModelWrapper posmodel = new ModelWrapper(new File("data\\model\\pos\\en-pos-maxent.bin"));		
		ModelWrapper chunkmodel= SyntacticAnalysisMEForChunk.train(trainFile, params, contextGen, encoding, aghw);
		ModelWrapper buildmodel = SyntacticAnalysisMEForBuildAndCheck.trainForBuild(trainFile, params, contextGen, encoding, aghw);
		ModelWrapper checkmodel = SyntacticAnalysisMEForBuildAndCheck.trainForCheck(trainFile, params, contextGen, encoding, aghw);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));
        SyntacticAnalysisForPos<HeadTreeNode> postagger;
        if(postaggertype.equals("china")){
			postagger = new SyntacticAnalysisMEForPosChinese(posmodel);
        }else{
        	postagger = new SyntacticAnalysisMEForPosEnglish(posmodel);
        }	
        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel, contextGen, aghw);
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildmodel, checkmodel, contextGen, aghw);
        
        SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
        SyntacticAnalysisEvaluatorForByStep evaluator = null;
        SyntacticAnalysisErrorPrinter printer = null;
        if(errorFile != null){
        	System.out.println("Print error to file " + errorFile);
        	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(errorFile));    	
        	evaluator = new SyntacticAnalysisEvaluatorForByStep(postagger, chunktagger, buildandchecktagger, aghw, printer);
        }else{
        	evaluator = new SyntacticAnalysisEvaluatorForByStep(postagger, chunktagger, buildandchecktagger, aghw);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> linesStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(linesStream, aghw);
        evaluator.evaluate(sampleStream);
        SyntacticAnalysisMeasure measureRes = evaluator.getMeasure();
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));
        System.out.println(measureRes);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1){
            usage();
            return;
        }
        String trainFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = "UTF-8";
        String type = "MAXENT";
        String postagger = "english";
        int cutoff = 3;
        int iters = 100;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                trainFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-type"))
            {
                type = args[i + 1];
                i++;
            }
            else if (args[i].equals("-postagger"))
            {
                postagger = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-error"))
            {
                errorFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
        if (errorFile != null)
        {
            eval(postagger, new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(postagger, new File(trainFile), params, new File(goldFile), encoding, null);
	}
}
