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

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.ar.core.Frame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Background rendering for an ARCore activing. This renders the camera texture in the backrgound of
 * the scene.
 */
class BackgroundRenderer {
  private ShaderProgram shader;
  private Mesh mesh;

  // The Shader class in GDX is aware of some common uniform and attribute names.
  // These are used to make setting the values when drawing "automatic".
  // This shader simply draws the OES texture on the provided coordinates.
  // a_position == ShaderProgram.POSITION_ATTRIBUTE
  private static final String vertexShaderCode =
      "attribute vec4 a_position;\n"
          +
          // a_texCoord0 == ShaderProgram.TEXCOORD_ATTRIBUTE + "0"
          "attribute vec2 a_texCoord0;\n"
          + "varying vec2 v_TexCoord;\n"
          + "void main() {\n"
          + "gl_Position = a_position;\n"
          + " v_TexCoord = a_texCoord0;\n"
          + "}";

  private static final String fragmentShaderCode =
      "#extension GL_OES_EGL_image_external : require\n"
          + "\n"
          + "precision mediump float;\n"
          + "varying vec2 v_TexCoord;\n"
          + "uniform samplerExternalOES sTexture;\n"
          + "\n"
          + "\n"
          + "void main() {\n"
          + "    gl_FragColor = texture2D(sTexture, v_TexCoord);\n"
          + "}";

  public BackgroundRenderer() {

    shader = new ShaderProgram(vertexShaderCode, fragmentShaderCode);

    mesh = new Mesh(true, 4, 0, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
  }

  public void render(Frame frame) {

    if (mesh.getNumVertices() == 0 || frame.hasDisplayGeometryChanged()) {
      mesh.setVertices(((ARCoreGraphics) Gdx.graphics).getBackgroundVertices(frame));
    }

    // Save the state of the glContext before drawing.
    GL20 gl = Gdx.gl;
    int[] saveFlags = new int[3];
    IntBuffer intbuf =
        ByteBuffer.allocateDirect(16 * Integer.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer();
    gl.glGetIntegerv(GL20.GL_DEPTH_TEST, intbuf);
    saveFlags[0] = intbuf.get(0);
    gl.glGetIntegerv(GL20.GL_DEPTH_WRITEMASK, intbuf);
    saveFlags[1] = intbuf.get(0);
    gl.glGetIntegerv(GL20.GL_DEPTH_FUNC, intbuf);
    saveFlags[2] = intbuf.get(0);

    // Disable depth, bind the texture and render it on the mesh.
    gl.glDisable(GLES20.GL_DEPTH_TEST);
    gl.glDepthMask(false);

    GLES20.glBindTexture(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES, ((ARCoreGraphics) Gdx.graphics).getBackgroundTexture());
    shader.begin();
    mesh.render(shader, GL20.GL_TRIANGLE_STRIP);
    shader.end();

    // Restore the state of the context.
    if (saveFlags[0] == GL20.GL_TRUE) {
      gl.glEnable(GL20.GL_DEPTH_TEST);
    }
    gl.glDepthMask(saveFlags[1] == GL20.GL_TRUE);
    gl.glDepthFunc(saveFlags[2]);
  }
}
