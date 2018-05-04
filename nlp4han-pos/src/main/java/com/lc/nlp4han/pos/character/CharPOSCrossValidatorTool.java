package com.lc.nlp4han.pos.character;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 词性标注交叉验证器
 * 
 * @author 刘小峰
 * 
 */
public class CharPOSCrossValidatorTool
{
    private static void usage()
    {
        System.out.println(CharPOSCrossValidatorTool.class.getName() + " -data <corpusFile> -encoding <encoding> -format <format> [-folds <nFolds>] " + "[-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws IOException
    {
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
        String algType = "MAXENT";
        String format = "open";
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
            else if (args[i].equals("-format"))
            {
            	format = args[i + 1];
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
            else if (args[i].equals("-type"))
            {
                algType = args[i + 1];
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, algType);
        
        CharPOSCrossValidation crossValidator = new CharPOSCrossValidation(params);

        CharPOSSampleParser parse = null;
        ObjectStream<String> lineStream = null;
        if(format.equals("open")){
        	parse = new CharPOSParseOpen();
        	lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        }else if(format.equals("news")){
        	parse = new CharPOSParseNews();
        	lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        }
        
        ObjectStream<CharPOSSample> sampleStream = new CharPOSSampleStream(lineStream, parse);
        CharPOSContextGenerator contextGen = new CharPOSContextGeneratorConf();

        crossValidator.evaluate(sampleStream, folds, contextGen);
    }
}
