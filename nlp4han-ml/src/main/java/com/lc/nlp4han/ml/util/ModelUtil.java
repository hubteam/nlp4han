package com.lc.nlp4han.ml.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.lc.nlp4han.ml.maxent.gis.GIS;
import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.GenericModelReader;
import com.lc.nlp4han.ml.model.GenericModelWriter;

/**
 * Utility class for handling of {@link ClassificationModel}s.
 */
public final class ModelUtil {

  private ModelUtil() {
    // not intended to be instantiated
  }

  /**
   * Writes the given model to the given {@link OutputStream}.
   *
   * This methods does not closes the provided stream.
   *
   * @param model the model to be written
   * @param out the stream the model should be written to
   *
   * @throws IOException
   * @throws IllegalArgumentException in case one of the parameters is null
   */
  public static void writeModel(ClassificationModel model, final OutputStream out)
          throws IOException, IllegalArgumentException {

    if (model == null)
      throw new IllegalArgumentException("model parameter must not be null!");

    if (out == null)
      throw new IllegalArgumentException("out parameter must not be null!");

    GenericModelWriter modelWriter = new GenericModelWriter((AbstractModel) model, new DataOutputStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        out.write(b);
      }
    }));
    modelWriter.persist();
  }
  
  public static ClassificationModel readModel(File modelFile)
          throws IOException, IllegalArgumentException {
    if (modelFile == null)
      throw new IllegalArgumentException("modelFile parameter must not be null!");

    GenericModelReader modelReader = new GenericModelReader(modelFile);
    
    
    return modelReader.getModel();
  }
  
  
  public static ClassificationModel readModel(InputStream input, boolean binary)
          throws IOException, IllegalArgumentException {
    if (input == null)
      throw new IllegalArgumentException("input parameter must not be null!");

    GenericModelReader modelReader = new GenericModelReader(input, binary);
    
    
    return modelReader.getModel();
  }
  
  public static ClassificationModel readModel(InputStream input)
          throws IOException, IllegalArgumentException {
    if (input == null)
      throw new IllegalArgumentException("input parameter must not be null!");

    GenericModelReader modelReader = new GenericModelReader(input);
    
    
    return modelReader.getModel();
  }

  /**
   * Checks if the expected outcomes are all contained as outcomes in the given model.
   *
   * @param model
   * @param expectedOutcomes
   *
   * @return true if all expected outcomes are the only outcomes of the model.
   */
  public static boolean validateOutcomes(ClassificationModel model, String... expectedOutcomes) {

    boolean result = true;

    if (expectedOutcomes.length == model.getNumOutcomes()) {

      Set<String> expectedOutcomesSet = new HashSet<String>();
      expectedOutcomesSet.addAll(Arrays.asList(expectedOutcomes));

      for (int i = 0; i < model.getNumOutcomes(); i++) {
        if (!expectedOutcomesSet.contains(model.getOutcome(i))) {
          result = false;
          break;
        }
      }
    }
    else {
      result = false;
    }

    return result;
  }

  /**
   * Writes the provided {@link InputStream} into a byte array
   * which is returned
   *
   * @param in stream to read data for the byte array from
   * @return byte array with the contents of the stream
   *
   * @throws IOException if an exception is thrown while reading
   *     from the provided {@link InputStream}
   */
  public static byte[] read(InputStream in) throws IOException {
    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

    int length;
    byte buffer[] = new byte[1024];
    while ((length = in.read(buffer)) > 0) {
      byteArrayOut.write(buffer, 0, length);
    }
    byteArrayOut.close();

    return byteArrayOut.toByteArray();
  }

//  public static void addCutoffAndIterations(Map<String, String> manifestInfoEntries,
//      int cutoff, int iterations) {
//    manifestInfoEntries.put(BaseModel.TRAINING_CUTOFF_PROPERTY, Integer.toString(cutoff));
//    manifestInfoEntries.put(BaseModel.TRAINING_ITERATIONS_PROPERTY, Integer.toString(iterations));
//  }

  /**
   * Creates the default training parameters in case they are not provided.
   *
   * Note: Do not use this method, internal use only!
   *
   *
   * @return training parameters instance
   */
  public static TrainingParameters createDefaultTrainingParameters() {
    TrainingParameters mlParams = new TrainingParameters();
    mlParams.put(TrainingParameters.ALGORITHM_PARAM, GIS.MAXENT_VALUE);
    mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(100));
    mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(5));

    return mlParams;
  }
}
