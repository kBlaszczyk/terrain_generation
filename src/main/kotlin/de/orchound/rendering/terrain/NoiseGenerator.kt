package de.orchound.rendering.terrain

import org.lwjgl.stb.STBPerlin
import java.nio.FloatBuffer


object NoiseGenerator {
	fun generateNoiseMap(width: Int, frequency: Float = 1f): NoiseMap {
		require(frequency > 0f)

		val data = FloatArray(width * width)
		val dataBuffer = FloatBuffer.wrap(data)

		for (y in 0 until width) {
			for (x in 0 until width) {
				val sampleX = x.toFloat() / (width - 1)
				val sampleY = y.toFloat() / (width - 1)
				val noiseValue = wrappedPerlinNoise(sampleX, sampleY, frequency)
				dataBuffer.put(noiseValue)
			}
		}

		return NoiseMap(width, data)
	}

	private fun wrappedPerlinNoise(x: Float, y: Float, frequency: Float): Float {
		val lacunarity = 2f
		val persistence = 0.5f

		var sampleFrequency = frequency
		var amplitude = 1f

		var noiseValue = 0f
		for (i in 0 .. 5) {
			val sampleX = sampleFrequency * x
			val sampleY = sampleFrequency * y
			val wrapValue = sampleFrequency.toInt()

			val perlinValue = STBPerlin.stb_perlin_noise3(
				sampleX, sampleY, 0f, wrapValue, wrapValue, 0
			)
			noiseValue += perlinValue * amplitude

			sampleFrequency *= lacunarity
			amplitude *= persistence
		}

		return noiseValue
	}
}
