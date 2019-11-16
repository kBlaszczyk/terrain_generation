package de.orchound.rendering.terrain

import de.orchound.rendering.Color
import de.orchound.rendering.opengl.OpenGLMesh
import de.orchound.rendering.opengl.OpenGLTexture
import de.orchound.rendering.opengl.OpenGLType
import java.nio.ByteBuffer


class TerrainGenerator(val width: Int, val terrainLayout: TerrainLayout) {

	fun generateTerrain(): TerrainSceneObject {
		val noiseMap = NoiseGenerator.generateNoiseMap(width, 6f)
		val heightMap = HeightMap(noiseMap.width, noiseMap.data)

		return TerrainSceneObject(createMesh(heightMap), createTexture(heightMap))
	}

	private fun createTexture(heightMap: HeightMap): OpenGLTexture {
		val data = ByteBuffer.allocateDirect(heightMap.width * heightMap.width * 3)
		heightMapToColors(heightMap, data)
		//heightMapToGreyscale(heightMap, data)
		return OpenGLTexture(heightMap.width, heightMap.width, data)
	}

	private fun heightMapToColors(heightMap: HeightMap, dest: ByteBuffer) {
		for (y in 0 until heightMap.width) {
			for (x in 0 until heightMap.width) {
				val color = terrainLayout.getColor(heightMap.getValue(x, y))
				dest.put(color.toRgbBytes())
			}
		}
		dest.flip()
	}

	private fun heightMapToGreyscale(heightMap: HeightMap, dest: ByteBuffer) {
		for (y in 0 until heightMap.width) {
			for (x in 0 until heightMap.width) {
				val normalizedHeight = heightMap.getValue(x, y) / heightMap.maxHeight
				val color = Color.fromNormalizedGrey(normalizedHeight)
				dest.put(color.toRgbBytes())
			}
		}
		dest.flip()
	}

	private fun createMesh(heightMap: HeightMap): OpenGLMesh {
		val mesh = OpenGLMesh()

		mesh.setVertexAttribute(
			byteArrayToInvertedEndiannessByteBuffer(createVertices(heightMap), 4),
			0, OpenGLType.FLOAT,3, false
		)

		mesh.setVertexAttribute(
			byteArrayToInvertedEndiannessByteBuffer(createUVs(heightMap.width), 4),
			1, OpenGLType.FLOAT, 2, false
		)

		mesh.setIndices(
			byteArrayToInvertedEndiannessByteBuffer(createIndices(heightMap.width), 4),
			OpenGLType.UNSIGNED_INT
		)

		return mesh
	}

	private fun createVertices(heightMap: HeightMap): ByteArray {
		val vertexData = ByteArray(heightMap.width * heightMap.height * 3 * 4)
		val vertexBuffer = ByteBuffer.wrap(vertexData).asFloatBuffer()

		for (z in 0 until heightMap.width) {
			for (x in 0 until heightMap.width) {
				vertexBuffer.put(x - heightMap.width / 2f)
				vertexBuffer.put(heightMap.getValue(x, z))
				vertexBuffer.put(z - heightMap.width / 2f)
			}
		}

		return vertexData
	}

	private fun createUVs(terrainWidth: Int): ByteArray {
		val uvData = ByteArray(terrainWidth * terrainWidth * 2 * 4)
		val uvBuffer = ByteBuffer.wrap(uvData).asFloatBuffer()

		for (z in 0 until terrainWidth) {
			for (x in 0 until terrainWidth) {
				uvBuffer.put(x.toFloat() / (width - 1))
				uvBuffer.put(z.toFloat() / (width - 1))
			}
		}

		return uvData
	}

	private fun createIndices(terrainWidth: Int): ByteArray {
		val indicesCount = (terrainWidth - 1) * (terrainWidth - 1) * 6
		val indexData = ByteArray(indicesCount * 4)
		val indexBuffer = ByteBuffer.wrap(indexData).asIntBuffer()
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

		return indexData
	}

	private fun byteArrayToInvertedEndiannessByteBuffer(data: ByteArray, formatSize: Int): ByteBuffer {
		val byteBuffer = ByteBuffer.allocateDirect(data.size)
		for (element in data.indices step formatSize) {
			for (index in formatSize - 1 downTo 0) {
				byteBuffer.put(data[element + index])
			}
		}
		byteBuffer.flip()
		return byteBuffer
	}
}
