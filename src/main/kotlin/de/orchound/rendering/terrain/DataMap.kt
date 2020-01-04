package de.orchound.rendering.terrain

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.GL_R32F


abstract class DataMap(val width: Int, val height: Int, val data: FloatArray) {

	init {
		require(data.size == width * height)
	}

	fun getValue(x: Int, y: Int): Float {
		return data[y * width + x]
	}
}

class NoiseMap(width: Int, data: FloatArray) : DataMap(width, width, data)

class HeightMap(width: Int, noise: FloatArray, heightFactor: Float) : DataMap(width, width, noise) {
	val handle = glGenTextures()

	private val maxHeight: Float

	init {
		var min = data[0]
		for (i in data.indices) {
			data[i] *= heightFactor
			if (data[i] < min)
				min = data[i]
		}

		var max = 0f
		for (i in data.indices) {
			data[i] -= min
			if (data[i] > max)
				max = data[i]
		}

		maxHeight = max

		glBindTexture(GL_TEXTURE_2D, handle)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, width, width, 0, GL_RED, GL_FLOAT, data)
	}
}
