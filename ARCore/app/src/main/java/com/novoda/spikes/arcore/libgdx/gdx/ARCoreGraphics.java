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

import android.view.Surface;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.google.ar.core.Frame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Extended AndroidGraphics that is ARCore aware. This handles creating an OES Texture an passing it
 * to the ARCore session.
 */
public class ARCoreGraphics extends AndroidGraphics {

  // TODO: refactor session to this class
  private BaseARCoreActivity application;
  private BackgroundRendererHelper mBackgroundRenderer;
  private AtomicReference<Frame> mCurrentFrame;

  public ARCoreGraphics(
      BaseARCoreActivity arCoreApplication,
      AndroidApplicationConfiguration config,
      ResolutionStrategy resolutionStrategy) {
    super(arCoreApplication, config, resolutionStrategy);
    application = arCoreApplication;

    mBackgroundRenderer = new BackgroundRendererHelper();
    mCurrentFrame = new AtomicReference<>(null);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    super.onSurfaceChanged(gl, width, height);
    WindowManager mgr = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      mgr = application.getSystemService(WindowManager.class);
    }
    int rotation = Surface.ROTATION_0;
    if (mgr != null) {
      rotation = mgr.getDefaultDisplay().getRotation();
    }
    application.getSessionSupport().setDisplayGeometry(rotation, width, height);
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    super.onSurfaceCreated(gl, config);
    mBackgroundRenderer.createOnGlThread(application);
    application.getSessionSupport().setCameraTextureName(mBackgroundRenderer.getTextureId());
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    super.onDrawFrame(gl);
    mCurrentFrame.set(null);
  }

  public int getBackgroundTexture() {
    return mBackgroundRenderer.getTextureId();
  }

  public float[] getBackgroundVertices(Frame frame) {
    return mBackgroundRenderer.getVertices(frame);
  }

  /**
   * Returns the current ARCore frame.  This is reset at the end of the render loop.
   */
  public Frame getCurrentFrame() {
    if (mCurrentFrame.get() == null) {
        mCurrentFrame.compareAndSet(null, application.getSessionSupport().update());
    }
    return mCurrentFrame.get();
  }
}
