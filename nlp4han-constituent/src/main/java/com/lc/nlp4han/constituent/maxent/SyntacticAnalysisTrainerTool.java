package com.lc.nlp4han.constituent.maxent;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.TrainingParameters;


/**
 * 句法分析训练模型运行类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisTrainerTool {

	private static void usage(){
		System.out.println(SyntacticAnalysisTrainerTool.class.getName()+"-data <corpusFile> -chunkmodel <chunkmodelFile> -buildmodel <buildmodelFile> -checkmodel <checkmodelFile> -type <algorithom>"
				+ "-encoding <encoding>"+"[-cutoff <num>] [-iters <num>]");
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length < 1){
			usage();
			return;
		}
		int cutoff = 3;
		int iters = 100;
        File corpusFile = null;
        File chunkmodelFile = null;
        File buildmodelFile = null;
        File checkmodelFile = null;
        String encoding = "UTF-8";
        String type = "MAXENT";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-chunkmodel"))
            {
                chunkmodelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-buildmodel"))
            {
            	buildmodelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-checkmodel"))
            {
            	checkmodelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-type"))
            {
                type = args[i + 1];
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
        
        SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
        System.out.println(contextGen);
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
        
        AbstractHeadGenerator headGenerator = new HeadGeneratorCollins();
        SyntacticAnalysisMEForChunk.train(corpusFile, chunkmodelFile, params, contextGen, encoding, headGenerator);
		SyntacticAnalysisMEForBuildAndCheck.trainForBuild(corpusFile, buildmodelFile, params, contextGen, encoding, headGenerator);
		SyntacticAnalysisMEForBuildAndCheck.trainForCheck(corpusFile, checkmodelFile, params, contextGen, encoding, headGenerator);
	}
}
