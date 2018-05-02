/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novoda.spikes.arcore.poly.copied;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.novoda.spikes.arcore.google.rendering.ObjectRenderer;
import com.novoda.spikes.arcore.google.rendering.ShaderUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

/**
 * Renders an object loaded from an OBJ file in OpenGL.
 */
public class PolyObjectRenderer {
  private static final String TAG = ObjectRenderer.class.getSimpleName();

  // Shader names.
  private static final String VERTEX_SHADER_NAME = "shaders/object.vert";
  private static final String FRAGMENT_SHADER_NAME = "shaders/object.frag";
  private Vec3 boundsMin;
  private Vec3 boundsMax;

  /**
   * Blend mode.
   *
   * @see #setBlendMode(BlendMode)
   */
  public enum BlendMode {
    /**
     * Multiplies the destination color by the source alpha.
     */
    Shadow,
    /**
     * Normal alpha blending.
     */
    Grid
  }

  ;

  private static final int COORDS_PER_VERTEX = 3;

  // Note: the last component must be zero to avoid applying the translational part of the matrix.
  private static final float[] LIGHT_DIRECTION = new float[]{0.0f, 1.0f, 0.0f, 0.0f};
  private float[] mViewLightDirection = new float[4];

  // Object vertex buffer variables.
  private int mVertexBufferId;
  private int mVerticesBaseAddress;
  private int mTexCoordsBaseAddress;
  private int mNormalsBaseAddress;
  private int mIndexBufferId;
  private int mIndexCount;

  private int mProgram;
  private int[] mTextures = new int[1];

  // Shader location: model view projection matrix.
  private int mModelViewUniform;
  private int mModelViewProjectionUniform;

  // Shader location: object attributes.
  private int mPositionAttribute;
  private int mNormalAttribute;
  private int mTexCoordAttribute;

  // Shader location: texture sampler.
  private int mTextureUniform;

  // Shader location: environment properties.
  private int mLightingParametersUniform;

  // Shader location: material properties.
  private int mMaterialParametersUniform;

  // Shader location: color correction property
  private int colorCorrectionParameterUniform;

  private BlendMode mBlendMode = null;

  // Temporary matrices allocated here to reduce number of allocations for each frame.
  private float[] mModelMatrix = new float[16];
  private float[] mModelViewMatrix = new float[16];
  private float[] mModelViewProjectionMatrix = new float[16];

  // Set some default material properties to use for lighting.
  private float mAmbient = 0.3f;
  private float mDiffuse = 1.0f;
  private float mSpecular = 1.0f;
  private float mSpecularPower = 6.0f;

  public PolyObjectRenderer() {
  }

  /**
   * Creates and initializes OpenGL resources needed for rendering the model.
   *
   * @param context      Context for loading the shader and below-named model and texture assets.
   * @param objBytes     A byte array representing an OBJ file to parse.
   * @param textureBytes A byte array representing a PNG file with the object's texture.
   */
  public void createOnGlThread(Context context, byte[] objBytes,
                               byte[] textureBytes) throws IOException {
    // Read the texture.
    Bitmap textureBitmap = BitmapFactory.decodeByteArray(textureBytes, 0, textureBytes.length);

    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(mTextures.length, mTextures, 0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);

    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                           GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR
    );
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                           GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
    );
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    textureBitmap.recycle();

    ShaderUtil.checkGLError(TAG, "Texture loading");

    // Read the obj file.
    ByteArrayInputStream objInputStream = new ByteArrayInputStream(objBytes);
    Obj obj = ObjReader.read(objInputStream);

    // Prepare the Obj so that its structure is suitable for
    // rendering with OpenGL:
    // 1. Triangulate it
    // 2. Make sure that texture coordinates are not ambiguous
    // 3. Make sure that normals are not ambiguous
    // 4. Convert it to single-indexed data
    obj = ObjUtils.convertToRenderable(obj);

    // OpenGL does not use Java arrays. ByteBuffers are used instead to provide data in a format
    // that OpenGL understands.

    String infoString = ObjUtils.createInfoString(obj);

    // Obtain the data from the OBJ, as direct buffers:
    IntBuffer wideIndices = ObjData.getFaceVertexIndices(obj, 3);
    FloatBuffer vertices = ObjData.getVertices(obj);
    FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
    FloatBuffer normals = ObjData.getNormals(obj);

    int verticesAll = 0;
    while (vertices.hasRemaining()) {
      float x = vertices.get();
      float y = vertices.get();
      float z = vertices.get();
      encapsulateInBounds(x, y, z);
      verticesAll++;
    }
    vertices.rewind();

    // Convert int indices to shorts for GL ES 2.0 compatibility
    ShortBuffer indices = ByteBuffer.allocateDirect(2 * wideIndices.limit())
            .order(ByteOrder.nativeOrder()).asShortBuffer();
    while (wideIndices.hasRemaining()) {
      indices.put((short) wideIndices.get());
    }
    indices.rewind();

    int[] buffers = new int[2];
    GLES20.glGenBuffers(2, buffers, 0);
    mVertexBufferId = buffers[0];
    mIndexBufferId = buffers[1];

    // Load vertex buffer
    mVerticesBaseAddress = 0;
    mTexCoordsBaseAddress = mVerticesBaseAddress + 4 * vertices.limit();
    mNormalsBaseAddress = mTexCoordsBaseAddress + 4 * texCoords.limit();
    final int totalBytes = mNormalsBaseAddress + 4 * normals.limit();

    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);
    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, totalBytes, null, GLES20.GL_STATIC_DRAW);
    GLES20.glBufferSubData(
            GLES20.GL_ARRAY_BUFFER, mVerticesBaseAddress, 4 * vertices.limit(), vertices);
    GLES20.glBufferSubData(
            GLES20.GL_ARRAY_BUFFER, mTexCoordsBaseAddress, 4 * texCoords.limit(), texCoords);
    GLES20.glBufferSubData(
            GLES20.GL_ARRAY_BUFFER, mNormalsBaseAddress, 4 * normals.limit(), normals);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    // Load index buffer
    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferId);
    mIndexCount = indices.limit();
    GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER, 2 * mIndexCount, indices, GLES20.GL_STATIC_DRAW);
    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

    ShaderUtil.checkGLError(TAG, "OBJ buffer load");

    final int vertexShader =
            ShaderUtil.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
    final int fragmentShader =
            ShaderUtil.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NAME);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertexShader);
    GLES20.glAttachShader(mProgram, fragmentShader);
    GLES20.glLinkProgram(mProgram);
    GLES20.glUseProgram(mProgram);

    ShaderUtil.checkGLError(TAG, "Program creation");

    mModelViewUniform = GLES20.glGetUniformLocation(mProgram, "u_ModelView");
    mModelViewProjectionUniform =
            GLES20.glGetUniformLocation(mProgram, "u_ModelViewProjection");

    mPositionAttribute = GLES20.glGetAttribLocation(mProgram, "a_Position");
    mNormalAttribute = GLES20.glGetAttribLocation(mProgram, "a_Normal");
    mTexCoordAttribute = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");

    mTextureUniform = GLES20.glGetUniformLocation(mProgram, "u_Texture");

    mLightingParametersUniform = GLES20.glGetUniformLocation(mProgram, "u_LightingParameters");
    mMaterialParametersUniform = GLES20.glGetUniformLocation(mProgram, "u_MaterialParameters");
    colorCorrectionParameterUniform =
            GLES20.glGetUniformLocation(mProgram, "u_ColorCorrectionParameters");

    ShaderUtil.checkGLError(TAG, "Program parameters");

    Matrix.setIdentityM(mModelMatrix, 0);
  }

  private void encapsulateInBounds(float x, float y, float z) {
    if (boundsMin == null) {
      boundsMin = new Vec3(x, y, z);
    } else {
      boundsMin.x = Math.min(boundsMin.x, x);
      boundsMin.y = Math.min(boundsMin.y, y);
      boundsMin.z = Math.min(boundsMin.z, z);
    }
    if (boundsMax == null) {
      boundsMax = new Vec3(x, y, z);
    } else {
      boundsMax.x = Math.max(boundsMax.x, x);
      boundsMax.y = Math.max(boundsMax.y, y);
      boundsMax.z = Math.max(boundsMax.z, z);
    }
  }

  /**
   * Represents a vector with 3 float components (x, y and z). Can be position or direction.
   */
  public static class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Vec3(Vec3 other) {
      this.x = other.x;
      this.y = other.y;
      this.z = other.z;
    }

    @Override
    public String toString() {
      return String.format("(%.3f, %.3f, %.3f)", x, y, z);
    }
  }

  /**
   * Selects the blending mode for rendering.
   *
   * @param blendMode The blending mode.  Null indicates no blending (opaque rendering).
   */
  public void setBlendMode(BlendMode blendMode) {
    mBlendMode = blendMode;
  }

  /**
   * Updates the object model matrix and applies scaling.
   *
   * @param modelMatrix A 4x4 model-to-world transformation matrix, stored in column-major order.
   * @see android.opengl.Matrix
   */
  public void updateModelMatrix(float[] modelMatrix, float assetDisplaySize) {

    // Because OBJs can have any size and the geometry can be at any point that's not necessarily
    // the origin, we apply a translation and scale to make sure it fits in a comfortable
    // bounding box in order for us to display it.
    Vec3 boundsCenter = getBoundsCenter();
    Vec3 boundsSize = getBoundsSize();
    float maxDimension = Math.max(boundsSize.x, Math.max(boundsSize.y, boundsSize.z));
    float scale = assetDisplaySize / maxDimension;
    Log.d(TAG, "Will apply no translation but scale " + scale);

    float[] scaleMatrix = new float[16];
    Matrix.setIdentityM(scaleMatrix, 0);
    scaleMatrix[0] = scale;
    scaleMatrix[5] = scale;
    scaleMatrix[10] = scale;

    Matrix.multiplyMM(mModelMatrix, 0, modelMatrix, 0, scaleMatrix, 0);
  }

  /**
   * Sets the surface characteristics of the rendered model.
   *
   * @param ambient       Intensity of non-directional surface illumination.
   * @param diffuse       Diffuse (matte) surface reflectivity.
   * @param specular      Specular (shiny) surface reflectivity.
   * @param specularPower Surface shininess.  Larger values result in a smaller, sharper
   *                      specular highlight.
   */
  public void setMaterialProperties(
          float ambient, float diffuse, float specular, float specularPower) {
    mAmbient = ambient;
    mDiffuse = diffuse;
    mSpecular = specular;
    mSpecularPower = specularPower;
  }

  /**
   * Draws the model.
   *
   * @param cameraView        A 4x4 view matrix, in column-major order.
   * @param cameraPerspective A 4x4 projection matrix, in column-major order.
   *                          properties.
   * @see #setBlendMode(BlendMode)
   * @see #updateModelMatrix(float[], float)
   * @see #setMaterialProperties(float, float, float, float)
   * @see android.opengl.Matrix
   */
  public void draw(float[] cameraView, float[] cameraPerspective, float[] colorCorrectionRgba) {

    ShaderUtil.checkGLError(TAG, "Before draw");

    // Build the ModelView and ModelViewProjection matrices
    // for calculating object position and light.
    Matrix.multiplyMM(mModelViewMatrix, 0, cameraView, 0, mModelMatrix, 0);
    Matrix.multiplyMM(mModelViewProjectionMatrix, 0, cameraPerspective, 0, mModelViewMatrix, 0);

    GLES20.glUseProgram(mProgram);

    // Set the lighting environment properties.
    Matrix.multiplyMV(mViewLightDirection, 0, mModelViewMatrix, 0, LIGHT_DIRECTION, 0);
    normalizeVec3(mViewLightDirection);
    GLES20.glUniform4f(mLightingParametersUniform,
                       mViewLightDirection[0], mViewLightDirection[1], mViewLightDirection[2], 1.f
    );

    GLES20.glUniform4f(
            colorCorrectionParameterUniform,
            colorCorrectionRgba[0],
            colorCorrectionRgba[1],
            colorCorrectionRgba[2],
            colorCorrectionRgba[3]
    );

    // Set the object material properties.
    GLES20.glUniform4f(mMaterialParametersUniform, mAmbient, mDiffuse, mSpecular,
                       mSpecularPower
    );

    // Attach the object texture.
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
    GLES20.glUniform1i(mTextureUniform, 0);

    // Set the vertex attributes.
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);

    GLES20.glVertexAttribPointer(
            mPositionAttribute, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVerticesBaseAddress);
    GLES20.glVertexAttribPointer(
            mNormalAttribute, 3, GLES20.GL_FLOAT, false, 0, mNormalsBaseAddress);
    GLES20.glVertexAttribPointer(
            mTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexCoordsBaseAddress);

    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    // Set the ModelViewProjection matrix in the shader.
    GLES20.glUniformMatrix4fv(
            mModelViewUniform, 1, false, mModelViewMatrix, 0);
    GLES20.glUniformMatrix4fv(
            mModelViewProjectionUniform, 1, false, mModelViewProjectionMatrix, 0);

    // Enable vertex arrays
    GLES20.glEnableVertexAttribArray(mPositionAttribute);
    GLES20.glEnableVertexAttribArray(mNormalAttribute);
    GLES20.glEnableVertexAttribArray(mTexCoordAttribute);

    if (mBlendMode != null) {
      GLES20.glDepthMask(false);
      GLES20.glEnable(GLES20.GL_BLEND);
      switch (mBlendMode) {
        case Shadow:
          // Multiplicative blending function for Shadow.
          GLES20.glBlendFunc(GLES20.GL_ZERO, GLES20.GL_ONE_MINUS_SRC_ALPHA);
          break;
        case Grid:
          // Grid, additive blending function.
          GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
          break;
      }
    }

    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferId);
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexCount, GLES20.GL_UNSIGNED_SHORT, 0);
    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

    if (mBlendMode != null) {
      GLES20.glDisable(GLES20.GL_BLEND);
      GLES20.glDepthMask(true);
    }

    // Disable vertex arrays
    GLES20.glDisableVertexAttribArray(mPositionAttribute);
    GLES20.glDisableVertexAttribArray(mNormalAttribute);
    GLES20.glDisableVertexAttribArray(mTexCoordAttribute);

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    ShaderUtil.checkGLError(TAG, "After draw");
  }

  public static void normalizeVec3(float[] v) {
    float reciprocalLength = 1.0f / (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    v[0] *= reciprocalLength;
    v[1] *= reciprocalLength;
    v[2] *= reciprocalLength;
  }

  /**
   * Returns the minimum coordinates of the object's axis-aligned bounding box.
   */
  public Vec3 getBoundsMin() {
    return boundsMin;
  }

  /**
   * Returns the maximum coordinates of the object's axis-aligned bounding box.
   */
  public Vec3 getBoundsMax() {
    return boundsMax;
  }

  /**
   * Returns the center of the object's axis-aligned bounding box.
   */
  public Vec3 getBoundsCenter() {
    return new Vec3((boundsMin.x + boundsMax.x) / 2f, (boundsMin.y + boundsMax.y) / 2f,
                    (boundsMin.z + boundsMax.z) / 2f
    );
  }

  /**
   * Returns the size of the object's axis-aligned bounding box.
   */
  public Vec3 getBoundsSize() {
    return new Vec3(boundsMax.x - boundsMin.x, boundsMax.y - boundsMin.y, boundsMax.z - boundsMin.z);
  }
}
