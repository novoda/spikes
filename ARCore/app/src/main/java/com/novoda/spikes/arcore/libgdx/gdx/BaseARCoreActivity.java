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

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationBase;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidApplicationLogger;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AndroidClipboard;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.backends.android.AndroidInputFactory;
import com.badlogic.gdx.backends.android.AndroidNet;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.novoda.spikes.arcore.libgdx.gdx.util.ARSessionSupport;

import java.lang.reflect.Method;

/**
 * Android Activity subclass that handles initializing ARCore and the underlying graphics engine
 * used for drawing 3d and 2d models in the context of the ARCore Frame. This class is based on the
 * libgdx library for Android game development.
 */
public class BaseARCoreActivity extends AndroidApplication implements LifecycleOwner,
        ARSessionSupport.StatusChangeListener {

  // ARCore specific stuff
  private ARSessionSupport sessionSupport;

  // Implement the LifecycleOwner interface since AndroidApplication does not extend AppCompatActivity.
  // All this means is forward the events to the lifecycleRegistry object.
  private LifecycleRegistry lifecycleRegistry;

  /**
   * Gets the ARCore session.  It can be null if the
   * permissions were not granted by the user or if the device does not support ARCore.
   */
  @NonNull
  public ARSessionSupport getSessionSupport() {
    return sessionSupport;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lifecycleRegistry = new LifecycleRegistry(this);
    lifecycleRegistry.markState(Lifecycle.State.CREATED);
    sessionSupport = new ARSessionSupport(this, lifecycleRegistry, this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    lifecycleRegistry.markState(Lifecycle.State.STARTED);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
  }

  protected void onPause() {
    super.onPause();
    lifecycleRegistry.markState(Lifecycle.State.STARTED);
  }

  @Override
  protected void onResume() {
    super.onResume();
    lifecycleRegistry.markState(Lifecycle.State.RESUMED);
  }

  /**
   * This method has to be called in the {@link Activity#onCreate(Bundle)} method. It sets up all
   * the things necessary to get input, render via OpenGL and so on. You can configure other aspects
   * of the application with the rest of the fields in the {@link AndroidApplicationConfiguration}
   * instance.
   *
   * @param listener the {@link ApplicationListener} implementing the program logic
   * @param config   the {@link AndroidApplicationConfiguration}, defining various settings of the
   *                 application (use accelerometer, etc.).
   */
  public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config) {
    init(listener, config, false);
  }

  private void init(
          ApplicationListener listener, AndroidApplicationConfiguration config, boolean isForView) {
    if (this.getVersion() < MINIMUM_SDK) {
      throw new GdxRuntimeException(
              "LibGDX requires Android API Level " + MINIMUM_SDK + " or later.");
    }
    setApplicationLogger(new AndroidApplicationLogger());
    graphics =
            new ARCoreGraphics(
                    this,
                    config,
                    config.resolutionStrategy == null
                            ? new FillResolutionStrategy()
                            : config.resolutionStrategy);
    input = AndroidInputFactory.newAndroidInput(this, this, graphics.getView(), config);
    audio = new AndroidAudio(this, config);
    this.getFilesDir(); // workaround for Android bug #10515463
    files = new AndroidFiles(this.getAssets(), this.getFilesDir().getAbsolutePath());
    net = new AndroidNet(this);
    this.listener = listener;
    this.handler = new Handler();
    this.useImmersiveMode = config.useImmersiveMode;
    this.hideStatusBar = config.hideStatusBar;
    this.clipboard = new AndroidClipboard(this);

    // Add a specialized audio lifecycle listener
    addLifecycleListener(
            new LifecycleListener() {

              @Override
              public void resume() {
                // No need to resume audio here
              }

              @Override
              public void pause() {
                //     audio.pause();
              }

              @Override
              public void dispose() {
                audio.dispose();
              }
            });

    Gdx.app = this;
    Gdx.input = this.getInput();
    Gdx.audio = this.getAudio();
    Gdx.files = this.getFiles();
    Gdx.graphics = this.getGraphics();
    Gdx.net = this.getNet();

    if (!isForView) {
      try {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      } catch (Exception ex) {
        log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
      }
      getWindow()
              .setFlags(
                      WindowManager.LayoutParams.FLAG_FULLSCREEN,
                      WindowManager.LayoutParams.FLAG_FULLSCREEN);
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      setContentView(graphics.getView(), createLayoutParams());
    }

    createWakeLock(config.useWakelock);
    hideStatusBar(this.hideStatusBar);
    useImmersiveMode(this.useImmersiveMode);
    if (this.useImmersiveMode && getVersion() >= Build.VERSION_CODES.KITKAT) {
      try {
        Class<?> vlistener =
                Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
        Object o = vlistener.newInstance();
        Method method = vlistener.getDeclaredMethod("createListener", AndroidApplicationBase.class);
        method.invoke(o, this);
      } catch (Exception e) {
        log("AndroidApplication", "Failed to create AndroidVisibilityListener", e);
      }
    }
  }

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    return lifecycleRegistry;
  }

  @Override
  public void onStatusChanged() {
    // Not used
  }
}
