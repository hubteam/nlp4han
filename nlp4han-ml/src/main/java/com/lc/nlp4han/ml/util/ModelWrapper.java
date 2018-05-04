package com.lc.nlp4han.ml.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;

public class ModelWrapper
{
    protected ClassificationModel model;
    protected SequenceClassificationModel<String> seqModel;
    protected int beamSize = 3;
    
    public static int DEFAULT_BEAM_SIZE = 3;

    public ModelWrapper(SequenceClassificationModel<String> seqModel)
    {
        if (seqModel == null)
        {
            throw new IllegalArgumentException("The seqModel param must not be null!");
        }

        this.seqModel = seqModel;
    }

    public ModelWrapper(ClassificationModel model)
    {
        this(model, DEFAULT_BEAM_SIZE);
    }

    public ModelWrapper(ClassificationModel model, int beamSize)
    {
        if (model == null)
        {
            throw new IllegalArgumentException("The model param must not be null!");
        }

        this.model = model;
        this.beamSize = beamSize;
    }

    public ModelWrapper(InputStream in) throws IOException
    {
        loadModel(in);
    }

    public ModelWrapper(File modelFile) throws IOException
    {
        try (InputStream in = new BufferedInputStream(new FileInputStream(modelFile)))
        {
            loadModel(in);
        }
    }

    public ModelWrapper(URL modelURL) throws IOException
    {
        try (InputStream in = new BufferedInputStream(modelURL.openStream()))
        {
            loadModel(in);
        }
    }

    private void loadModel(InputStream in) throws IOException
    {

        if (in == null)
        {
            throw new IllegalArgumentException("in must not be null!");
        }

        if (!in.markSupported())
        {
            in = new BufferedInputStream(in);
        }

        model = ModelUtil.readModel(in);
    }

    public final void serialize(OutputStream out) throws IOException
    {
        ModelUtil.writeModel(model, out);
    }

    /**
     * 得到分类模型
     * 
     * @return 分类模型
     */
    public ClassificationModel getModel()
    {
        return model;
    }

    /**
     * 得到序列分类模型或构造序列分类模型
     * 
     * @return 序列分类模型
     */
    public SequenceClassificationModel<String> getSequenceModel()
    {
        if (seqModel != null)
            return seqModel;
        else if (model != null)
        {
            return new BeamSearch<>(beamSize, model);
        }
        else
            return null;

    }

    public int getBeamSize()
    {
        return beamSize;
    }
}
