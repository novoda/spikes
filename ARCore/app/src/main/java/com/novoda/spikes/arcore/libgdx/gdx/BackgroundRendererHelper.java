/*
Copyright 2017 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.novoda.spikes.arcore.libgdx.gdx;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.google.ar.core.Frame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Sets up the OES texture used to render the camera. This is refactored out from the rendering of
 * the background to decouple the backgrond processing, such a rotation, from the actual rendering.
 */
public class BackgroundRendererHelper {

  private static final int COORDS_PER_VERTEX = 3;
  private static final int TEXCOORDS_PER_VERTEX = 2;
  private static final int FLOAT_SIZE = 4;

  private FloatBuffer quadVertices;
  private FloatBuffer quadTexCoord;
  private FloatBuffer quadTexCoordTransformed;

  private int mTextureId = -1;
  private int mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES; // TODO(avirodov): configurable.

  public BackgroundRendererHelper() {}

  public int getTextureId() {
    return mTextureId;
  }

  public void createOnGlThread(Context context) {

    // Generate the background texture.
    int textures[] = new int[1];
    GLES20.glGenTextures(1, textures, 0);
    mTextureId = textures[0];
    GLES20.glBindTexture(mTextureTarget, mTextureId);
    GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
    GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

    int numVertices = 4;
    if (numVertices != QUAD_COORDS.length / COORDS_PER_VERTEX) {
      throw new RuntimeException("Unexpected number of vertices in BackgroundRenderer.");
    }

    ByteBuffer bbVertices = ByteBuffer.allocateDirect(QUAD_COORDS.length * FLOAT_SIZE);
    bbVertices.order(ByteOrder.nativeOrder());
    quadVertices = bbVertices.asFloatBuffer();
    quadVertices.put(QUAD_COORDS);
    quadVertices.position(0);

    ByteBuffer bbTexCoords =
        ByteBuffer.allocateDirect(numVertices * TEXCOORDS_PER_VERTEX * FLOAT_SIZE);
    bbTexCoords.order(ByteOrder.nativeOrder());
    quadTexCoord = bbTexCoords.asFloatBuffer();
    quadTexCoord.put(QUAD_TEXCOORDS);
    quadTexCoord.position(0);

    ByteBuffer bbTexCoordsTransformed =
        ByteBuffer.allocateDirect(numVertices * TEXCOORDS_PER_VERTEX * FLOAT_SIZE);
    bbTexCoordsTransformed.order(ByteOrder.nativeOrder());
    quadTexCoordTransformed = bbTexCoordsTransformed.asFloatBuffer();
  }

  public static final float[] QUAD_COORDS =
      new float[] {
        -1.0f, -1.0f, 0.0f, -1.0f, +1.0f, 0.0f, +1.0f, -1.0f, 0.0f, +1.0f, +1.0f, 0.0f,
      };

  public static final float[] QUAD_TEXCOORDS =
      new float[] {
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
      };

  public float[] getVertices(Frame frame) {
    if (frame != null && frame.hasDisplayGeometryChanged()) {
      frame.transformDisplayUvCoords(quadTexCoord, quadTexCoordTransformed);
    }
    float[] ret = new float[QUAD_COORDS.length + QUAD_TEXCOORDS.length];
    for (int i = 0; i < 4; i++) {
      ret[(i * 5) + 0] = QUAD_COORDS[i * 3];
      ret[(i * 5) + 1] = QUAD_COORDS[(i * 3) + 1];
      ret[(i * 5) + 2] = QUAD_COORDS[(i * 3) + 2];
      ret[(i * 5) + 3] = quadTexCoordTransformed.get((i * 2));
      ret[(i * 5) + 4] = quadTexCoordTransformed.get((i * 2) + 1);
    }
    return ret;
  }
}
