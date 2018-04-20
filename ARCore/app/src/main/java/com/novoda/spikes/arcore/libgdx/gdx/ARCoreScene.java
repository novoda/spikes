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

import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Matrix4;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;

/**
 * ARCoreScene is the base class for the scene to render. Application specific scenes extend this
 * class to handle input and create and manipulate models which are rendered in a batch at the end
 * of each frame.
 *
 * <p>This class handles the basic boilerplate of rendering the background image, moving the camera
 * based on the ARCore frame pose, and basic batch rendering.
 */
public abstract class ARCoreScene implements ApplicationListener {

  // The camera which is controlled by the ARCore pose.
  private PerspectiveCamera camera;
  // Renderer for the camera image which is the background for the ARCore app.
  private BackgroundRenderer backgroundRenderer;
  // Drawing batch.
  private ModelBatch modelBatch;

  /**
   * Called to render the scene and provide the current ARCore frame.
   *
   * @param frame - The ARCore frame.
   */
  protected abstract void render(Frame frame, ModelBatch modelBatch);

  /**
   * Camera controlled by ARCore. This is used to determine where the user is looking.
   */
  protected PerspectiveCamera getCamera() {
    return camera;
  }

  /**
   * Shader provider for creating shaders that are used by custom materials. It is protected access
   * to allow overriding to inject other shaders.
   */
  protected ShaderProvider createShaderProvider() {
    return new SimpleShaderProvider();
  }

  /**
   * ARCore session object.
   */
  protected Session getSession() {
    return ((BaseARCoreActivity) Gdx.app).getSessionSupport().getSession();
  }

  /**
   * Gets the Android View being used for rendering.
   */
  protected View getView() {
    return ((ARCoreGraphics) Gdx.graphics).getView();
  }

  @Override
  public void create() {
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(0, 1.6f, 0f);
    camera.lookAt(0, 0, 1f);
    camera.near = .01f;
    camera.far = 30f;
    camera.update();

    backgroundRenderer = new BackgroundRenderer();

    // TODO(wilkinsonclay): make a better shader provider.
    modelBatch = new ModelBatch(createShaderProvider());
  }

  @Override
  public void resize(int width, int height) {}

  @Override
  public void render() {

    // Boiler plater rendering code goes here, the intent is that this sets up the scene object,
    // Application specific rendering should be done from render(Frame).
    ARCoreGraphics arCoreGraphics = (ARCoreGraphics) Gdx.graphics;
    Frame frame = arCoreGraphics.getCurrentFrame();

    // Frame can be null when initializing or if ARCore is not supported on this device.
    if (frame == null) {
      return;
    }

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    backgroundRenderer.render(frame);

    Gdx.gl.glDepthMask(true);
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glEnable(GL20.GL_CULL_FACE);

    // Move the camera, and then render.
    float vm[] = new float[16];

    frame.getCamera().getProjectionMatrix(vm, 0, camera.near, camera.far);
    camera.projection.set(vm);
    frame.getCamera().getViewMatrix(vm, 0);
    camera.view.set(vm);
    camera.combined.set(camera.projection);
    Matrix4.mul(camera.combined.val, camera.view.val);

    // Here is the rendering batch.
    modelBatch.begin(camera);
    render(frame, modelBatch);
    modelBatch.end();
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void dispose() {}
}
