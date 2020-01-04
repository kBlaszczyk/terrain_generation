package de.orchound.rendering.terrain

import de.orchound.rendering.vec3Normalization
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.GL_RGB32F
import java.nio.FloatBuffer
import java.nio.IntBuffer


class NormalMap(heightMap: HeightMap) {

	val handle = glGenTextures()

	init {
		val mapWidth = heightMap.width / 8f
		val vertexData = createVertices(heightMap, mapWidth)
		val indices = createIndices(heightMap.width)
		val normalData = createNormals(vertexData, indices, heightMap.width)

		glBindTexture(GL_TEXTURE_2D, handle)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexImage2D(
			GL_TEXTURE_2D, 0, GL_RGB32F, heightMap.width, heightMap.width,
			0, GL_RGB, GL_FLOAT, normalData
		)
	}

	private fun createVertices(heightMap: HeightMap, width: Float): FloatArray {
		val vertexData = FloatArray(heightMap.width * heightMap.width * 3)
		val vertexBuffer = FloatBuffer.wrap(vertexData)

		val stepSize = width / (heightMap.width - 1)

		for (z in 0 until heightMap.width) {
			for (x in 0 until heightMap.width) {
				vertexBuffer.put(x * stepSize - width / 2f)
				vertexBuffer.put(heightMap.getValue(x, z))
				vertexBuffer.put(z * stepSize - width / 2f)
			}
		}

		return vertexData
	}

	private fun createIndices(terrainWidth: Int): IntArray {
		val indicesCount = (terrainWidth - 1) * (terrainWidth - 1) * 6
		val indices = IntArray(indicesCount)
		val indexBuffer = IntBuffer.wrap(indices)

		for (z in 0 until terrainWidth - 1) {
			for (x in 0 until terrainWidth - 1) {
				val xzOffset = z * terrainWidth + x
				indexBuffer.put(xzOffset)
				indexBuffer.put(xzOffset + terrainWidth)
				indexBuffer.put(xzOffset + terrainWidth + 1)
				indexBuffer.put(xzOffset)
				indexBuffer.put(xzOffset + terrainWidth + 1)
				indexBuffer.put(xzOffset + 1)
			}
		}

		return indices
	}

	private fun createNormals(vertexData: FloatArray, indices: IntArray, vertexWidth: Int): FloatArray {
		val normalData = FloatArray(vertexData.size)

		for (triangle in indices.indices step 3) {
			val triangleVertices = (0 .. 2).map {
				val vertexIndex = indices[triangle + it] * 3
				val vertex = Vector3f(
					vertexData[vertexIndex], vertexData[vertexIndex + 1], vertexData[vertexIndex + 2]
				)
				Pair(vertexIndex, vertex)
			}

			val normal = triangleVertices[1].second.sub(triangleVertices[0].second)
				.cross(triangleVertices[2].second.sub(triangleVertices[0].second)).normalize()

			for (vertex in triangleVertices)
				addNormalToVertex(normal, normalData, vertex.first)
		}

		for (i in 0 until vertexWidth) {
			combineNormals(i, vertexWidth * (vertexWidth - 1) + i, normalData)
			combineNormals(i * vertexWidth, i * vertexWidth + (vertexWidth - 1), normalData)
		}

		normalData.vec3Normalization()
		return normalData
	}

	private fun combineNormals(vertexIndex1: Int, vertexIndex2: Int, normalData: FloatArray) {
		val normalIndex1 = vertexIndex1 * 3
		val normalIndex2 = vertexIndex2 * 3
		val normal = Vector3f(
			normalData[normalIndex1], normalData[normalIndex1 + 1], normalData[normalIndex1 + 2]
		).add(
			normalData[normalIndex2], normalData[normalIndex2 + 1], normalData[normalIndex2 + 2]
		)

		for (i in 0 .. 2) {
			normalData[normalIndex1 + i] = normal[i]
			normalData[normalIndex2 + i] = normal[i]
		}
	}

	private fun addNormalToVertex(normal: Vector3f, normalData: FloatArray, vertexIndex: Int) {
		normalData[vertexIndex] += normal.x
		normalData[vertexIndex + 1] += normal.y
		normalData[vertexIndex + 2] += normal.z
	}
}
