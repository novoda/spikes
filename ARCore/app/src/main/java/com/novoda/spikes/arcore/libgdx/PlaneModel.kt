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

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.google.ar.core.Plane
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Procedural model based on the bounding polygon from a Plane detected by ARCore. This creates a
 * two parts of the polygon, the outer boundary, and an inner boundary. The custom shader then fades
 * the alpha between the inner and outer polygons.
 */
internal object PlaneModel {
    private val BYTES_PER_FLOAT = java.lang.Float.SIZE / 8
    private val BYTES_PER_SHORT = java.lang.Short.SIZE / 8
    private val COORDS_PER_VERTEX = 3 // x, z, alpha

    private val VERTS_PER_BOUNDARY_VERT = 2
    private val INDICES_PER_BOUNDARY_VERT = 3
    private val INITIAL_BUFFER_BOUNDARY_VERTS = 64

    private val INITIAL_VERTEX_BUFFER_SIZE_BYTES =
            BYTES_PER_FLOAT * COORDS_PER_VERTEX * VERTS_PER_BOUNDARY_VERT * INITIAL_BUFFER_BOUNDARY_VERTS

    private val INITIAL_INDEX_BUFFER_SIZE_BYTES = (
            BYTES_PER_SHORT
                    * INDICES_PER_BOUNDARY_VERT
                    * INDICES_PER_BOUNDARY_VERT
                    * INITIAL_BUFFER_BOUNDARY_VERTS)
    private val FADE_RADIUS_M = 0.25f

    fun createPlane(plane: Plane, index: Int): Model {
        val boundary = plane.polygon
        val extentX = plane.extentX
        val extentZ = plane.extentZ

        // Model builder is used to create mesh parts.
        val builder = ModelBuilder()

        val material = PlaneMaterial(index)

        builder.begin()
        val meshPartBuilder = builder.part(
                "plane" + index, GL20.GL_TRIANGLE_STRIP, VertexAttributes.Usage.Position.toLong(), material)

        meshPartBuilder.setUVRange(0f, 0f, 1f, 1f)
        var mVertexBuffer = ByteBuffer.allocateDirect(INITIAL_VERTEX_BUFFER_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        var mIndexBuffer = ByteBuffer.allocateDirect(INITIAL_INDEX_BUFFER_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()

        // Generate a new set of vertices and a corresponding triangle strip index set so that
        // the plane boundary polygon has a fading edge. This is done by making a copy of the
        // boundary polygon vertices and scaling it down around center to push it inwards. Then
        // the index buffer is setup accordingly.
        boundary.rewind()
        val boundaryVertices = boundary.limit() / 2
        val numVertices: Int
        val numIndices: Int

        numVertices = boundaryVertices * VERTS_PER_BOUNDARY_VERT
        // drawn as GL_TRIANGLE_STRIP with 3n-2 triangles (n-2 for fill, 2n for perimeter).
        numIndices = boundaryVertices * INDICES_PER_BOUNDARY_VERT

        if (mVertexBuffer.capacity() < numVertices * COORDS_PER_VERTEX) {
            var size = mVertexBuffer.capacity()
            while (size < numVertices * COORDS_PER_VERTEX) {
                size *= 2
            }
            mVertexBuffer = ByteBuffer.allocateDirect(BYTES_PER_FLOAT * size)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
        }
        mVertexBuffer.rewind()
        mVertexBuffer.limit(numVertices * COORDS_PER_VERTEX)

        if (mIndexBuffer.capacity() < numIndices) {
            var size = mIndexBuffer.capacity()
            while (size < numIndices) {
                size *= 2
            }
            mIndexBuffer = ByteBuffer.allocateDirect(BYTES_PER_SHORT * size)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
        }
        mIndexBuffer.rewind()
        mIndexBuffer.limit(numIndices)

        // Note: when either dimension of the bounding box is smaller than 2*FADE_RADIUS_M we
        // generate a bunch of 0-area triangles.  These don't get rendered though so it works
        // out ok.
        val xScale = Math.max((extentX - 2 * FADE_RADIUS_M) / extentX, 0.0f)
        val zScale = Math.max((extentZ - 2 * FADE_RADIUS_M) / extentZ, 0.0f)

        while (boundary.hasRemaining()) {
            val x = boundary.get()
            val z = boundary.get()
            // Each vertex has the X and Z value (Z is stored in the "Y" position) and the alpha for the
            // the vertex is in the "Z" value.  The outer polygon has an alpha of 0; the inner a value of
            // 1.
            mVertexBuffer.put(x)
            mVertexBuffer.put(z)
            mVertexBuffer.put(0.0f)
            mVertexBuffer.put(x * xScale)
            mVertexBuffer.put(z * zScale)
            mVertexBuffer.put(1.0f)
        }

        // step 1, perimeter
        mIndexBuffer.put(((boundaryVertices - 1) * 2).toShort())
        for (i in 0 until boundaryVertices) {
            mIndexBuffer.put((i * 2).toShort())
            mIndexBuffer.put((i * 2 + 1).toShort())
        }
        mIndexBuffer.put(1.toShort())
        // This leaves us on the interior edge of the perimeter between the inset vertices
        // for boundary verts n-1 and 0.

        // step 2, interior:
        for (i in 1 until boundaryVertices / 2) {
            mIndexBuffer.put(((boundaryVertices - 1 - i) * 2 + 1).toShort())
            mIndexBuffer.put((i * 2 + 1).toShort())
        }
        if (boundaryVertices % 2 != 0) {
            mIndexBuffer.put((boundaryVertices / 2 * 2 + 1).toShort())
        }

        mVertexBuffer.rewind()
        mIndexBuffer.rewind()

        val v = FloatArray(mVertexBuffer.limit())
        mVertexBuffer.get(v)
        val ind = ShortArray(mIndexBuffer.limit())
        mIndexBuffer.get(ind)
        meshPartBuilder.addMesh(v, ind, 0, ind.size)

        return builder.end()
    }
}
