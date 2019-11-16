package de.orchound.rendering.terrain

import de.orchound.rendering.opengl.OpenGLTexture
import java.nio.ByteBuffer
import kotlin.math.abs


class NoiseTexture(noise: NoiseMap) : OpenGLTexture(noise.width, noise.width, noiseMapToWave(noise)) {

	companion object {
		private fun noiseMapToWave(noise: NoiseMap): ByteBuffer {
			val backgroundGreyScale: Byte = 0xFF.toByte()
			val graphGreyScale: Byte = 0

			val data = ByteBuffer.allocateDirect(noise.width * noise.width * 3)
			for (y in 0 until noise.width) {
				val pixelHeight = -(y.toFloat() / (noise.width - 1) * 2f - 1f)
				for (x in 0 until noise.width) {
					val noiseValue = noise.getValue(x, 400)
					val greyScaleValue = if (abs(pixelHeight - noiseValue) < 0.01f)
						graphGreyScale
					else backgroundGreyScale
					data.put(ByteArray(3) { greyScaleValue })
				}
			}
			data.flip()

			return data
		}
	}
}

