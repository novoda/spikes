/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.novoda.spikes.arcore.google.tensorflow;

import android.app.Activity;

import java.io.IOException;

/**
 * This classifier works with the quantized MobileNet model.
 */
public class ImageClassifierQuantizedMobileNet extends ImageClassifier {

  /**
   * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
   * This isn't part of the super class, because we need a primitive array here.
   */
  private byte[][] labelProbArray = null;

  /**
   * Initializes an {@code ImageClassifier}.
   *
   * @param activity
   */
  public ImageClassifierQuantizedMobileNet(Activity activity) throws IOException {
    super(activity);
    labelProbArray = new byte[1][getNumLabels()];
  }

  @Override
  public String getModelPath() {
    // you can download this file from
    // https://storage.googleapis.com/download.tensorflow.org/models/tflite/mobilenet_v1_224_android_quant_2017_11_08.zip
    return "mobilenet_quant_v1_224.tflite";
  }

  @Override
  public String getLabelPath() {
    return "labels_mobilenet_quant_v1_224.txt";
  }

  @Override
  public int getImageSizeX() {
    return 224;
  }

  @Override
  public int getImageSizeY() {
    return 224;
  }

  @Override
  public int getNumBytesPerChannel() {
    // the quantized model uses a single byte only
    return 1;
  }

  @Override
  protected void addPixelValue(int pixelValue) {
    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
    imgData.put((byte) (pixelValue & 0xFF));
  }

  @Override
  protected void addByteValue(byte b) {
    imgData.put(b);
  }

  @Override
  protected float getProbability(int labelIndex) {
    return labelProbArray[0][labelIndex];
  }

  @Override
  protected void setProbability(int labelIndex, Number value) {
    labelProbArray[0][labelIndex] = value.byteValue();
  }

  @Override
  protected float getNormalizedProbability(int labelIndex) {
    return (labelProbArray[0][labelIndex] & 0xff) / 255.0f;
  }

  @Override
  protected void runInference() {
    tflite.run(imgData, labelProbArray);
  }
}
