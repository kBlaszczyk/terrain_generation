package de.orchound.rendering.terrain

import org.lwjgl.stb.STBPerlin
import java.nio.FloatBuffer


object NoiseGenerator {
	fun generateNoiseMap(width: Int, height: Int, scale: Float = 1f): NoiseMap {
		require(scale > 0f)

		val data = FloatArray(width * height)
		val dataBuffer = FloatBuffer.wrap(data)

		for (y in 0 until height) {
			for (x in 0 until width) {
				val sampleX = scale * x / (width - 1)
				val sampleY = scale * y / (height - 1)

				val perlinValue = STBPerlin.stb_perlin_fbm_noise3(
					sampleX, sampleY, 0f, 3f, 0.3f, 3
				)
				dataBuffer.put(perlinValue)
			}
		}

		return NoiseMap(width, data)
	}
}
