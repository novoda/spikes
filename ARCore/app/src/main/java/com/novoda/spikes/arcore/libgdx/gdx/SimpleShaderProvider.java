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

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

/**
 * Simple shader provider that gives an extension point to register new shaders. TODO: THere must be
 * a better way to do this....
 */
public class SimpleShaderProvider extends BaseShaderProvider {

  public void registerShader(Shader shader) {
    this.shaders.add(shader);
  }

  @Override
  protected Shader createShader(Renderable renderable) {
    return new DefaultShader(renderable);
  }
}
