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
package com.novoda.spikes.arcore.libgdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute

/**
 * Material for rendering the detected planes. This is an example of a material using a custom
 * shader.
 */
class PlaneMaterial(index: Int) : Material() {

    init {
        if (gridTexture == null) {
            gridTexture = Texture("models/trigrid.png")
            gridTexture!!.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        }
        set(TextureAttribute.createDiffuse(gridTexture))
        id = MATERIAL_ID_PREFIX + index
        set(BlendingAttribute(true, GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA, 1f))
        // Custom shader uniform values.
        set(PlaneShaderAttributes.createDotColor(COLORS[index % COLORS.size]))
        set(PlaneShaderAttributes.createLineColor(COLORS[(index + 1) % COLORS.size]))
        set(PlaneShaderAttributes.createIndexAttribute(index))

        // Not really a color, but controls how to draw/fade the grid.
        val gridControl = Color(0.2f, 0.4f, 2.0f, 1.5f)
        set(PlaneShaderAttributes.createGridControl(gridControl))
    }

    /**
     * Attributes and uniform values used by the Plane shader. This class is used to register them
     * with the GDX renderer. The values for the attributes are set on the material of the renderable,
     * or in the rendercontext used when rendering. Each attribute also has a setter method that is
     * used to retrieve the attribute value and set it in the shader object at the correct location.
     */
    internal class PlaneShaderAttributes(type: Long, // This attribute type has only one value, a color.
                                         val color: Color) : Attribute(type) {

        override fun copy(): Attribute {
            return PlaneShaderAttributes(type, color)
        }

        override fun compareTo(o: Attribute): Int {
            return if (type != o.type) {
                (type - o.type).toInt()
            } else (o as ColorAttribute).color.toIntBits() - color.toIntBits()
        }

        companion object {

            private val DotColorAlias = "u_dotColor"
            private val DotColorType = Attribute.register(DotColorAlias)
            private val LineColorAlias = "u_lineColor"
            private val LineColorType = Attribute.register(LineColorAlias)
            private val GridControlAlias = "u_gridControl"
            private val GridControlType = Attribute.register(GridControlAlias)
            private val IndexAlias = "u_index;"
            private val IndexType = Attribute.register(IndexAlias)

            fun createDotColor(color: Color): PlaneShaderAttributes {
                return PlaneShaderAttributes(DotColorType, color)
            }

            fun createGridControl(color: Color): PlaneShaderAttributes {
                return PlaneShaderAttributes(GridControlType, color)
            }

            fun createLineColor(color: Color): PlaneShaderAttributes {
                return PlaneShaderAttributes(LineColorType, color)
            }

            // Store the index in the red component of the color..
            fun createIndexAttribute(index: Int): PlaneShaderAttributes {
                return PlaneShaderAttributes(IndexType, Color(index.toFloat(), 0f, 0f, 0f))
            }
        }
    }

    companion object {

        // Id prefix used to detect this is a Plane material and it should be rendered using the Plane
        // shader.
        val MATERIAL_ID_PREFIX = "planeMat"

        private val COLORS = arrayOf(Color.BLACK, Color.CHARTREUSE, Color.CORAL, Color.CYAN, Color.BLUE, Color.FIREBRICK, Color.MAROON, Color.BROWN, Color.GOLDENROD, Color.PURPLE)

        private var gridTexture: Texture? = null
    }
}
