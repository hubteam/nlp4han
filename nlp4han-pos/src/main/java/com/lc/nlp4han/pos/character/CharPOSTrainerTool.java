package com.lc.nlp4han.pos.character;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

public class CharPOSTrainerTool
{
    private static void usage()
    {
        System.out.println(CharPOSTrainerTool.class.getName() + " -data <corpusFile> -model <modelFile> -format <format> -encoding <encoding> " + "[-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        int cutoff = 3;
        int iters = 100;
        File corpusFile = null;
        File modelFile = null;
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
            else if (args[i].equals("-model"))
            {
                modelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-format"))
            {
            	format = args[i + 1];
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
        ModelWrapper model = CharPOSTaggerME.train(sampleStream, params, contextGen);
        OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
        model.serialize(modelOut);
        modelOut.close();
    }
}
