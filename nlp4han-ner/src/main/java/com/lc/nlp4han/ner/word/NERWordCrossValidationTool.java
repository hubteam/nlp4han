package com.lc.nlp4han.ner.word;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ner.NEREvaluateMonitor;
import com.lc.nlp4han.ner.NERMeasure;

/**
 * 基于词的命名实体识别交叉验证类
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class NERWordCrossValidationTool {
    
    public void evaluate(ObjectStream<NERWordOrCharacterSample> samples, int nFolds, NERWordContextGenerator contextGen,
    		TrainingParameters trainParam, NEREvaluateMonitor... listeners) throws IOException{
    	CrossValidationPartitioner<NERWordOrCharacterSample> partitioner = new CrossValidationPartitioner<NERWordOrCharacterSample>(samples, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<NERWordOrCharacterSample> trainingSampleStream = partitioner.next();
			
			//训练模型
			trainingSampleStream.reset();
			ModelWrapper model = NERWordME.train(trainingSampleStream, trainParam, contextGen);

			NERWordEvaluator evaluator = new NERWordEvaluator(new NERWordME(model, contextGen), listeners);
			NERMeasure measure = new NERMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
    }
    
    private static void usage(){
    	System.out.println(NERWordCrossValidationTool.class.getName() + " -data <corpusFile> -encoding <encoding> " + "[-cutoff <num>] [-iters <num>] [-folds <nFolds>] ");
    }
    
    public static void main(String[] args) throws IOException {
    	if (args.length < 1)
        {
            usage();
            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        File corpusFile = null;
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
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
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        
        NERWordContextGenerator context = new NERWordContextGeneratorConf();
        System.out.println(context);
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding); 
        // TODO: 根据命令行参数选择不同的样本解析类
        NERParseStrategy parse = new NERParseWordPD();
        ObjectStream<NERWordOrCharacterSample> sampleStream = new NERWordSampleStream(lineStream, parse);
        NERWordCrossValidationTool run = new NERWordCrossValidationTool();
        run.evaluate(sampleStream,folds,context, params);
	}
}
