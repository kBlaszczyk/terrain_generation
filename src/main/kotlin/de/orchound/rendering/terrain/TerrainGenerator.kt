package de.orchound.rendering.terrain

import de.orchound.rendering.Color
import de.orchound.rendering.opengl.OpenGLTexture
import java.nio.ByteBuffer


class TerrainGenerator(val terrainLayout: TerrainLayout) {

	fun generateTerrain(): TerrainSceneObject {
		val noiseMap = NoiseGenerator.generateNoiseMap(256, 6f)
		val heightMap = HeightMap(noiseMap.width, noiseMap.data)
		return TerrainSceneObject(createTexture(heightMap))
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
}
