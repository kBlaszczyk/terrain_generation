package de.orchound.rendering.terrain


abstract class DataMap(val width: Int, val height: Int, val data: FloatArray) {

	init {
		require(data.size == width * height)
	}

	fun getValue(x: Int, y: Int): Float {
		return data[y * width + x]
	}
}

class NoiseMap(width: Int, data: FloatArray) : DataMap(width, width, data)

class HeightMap(width: Int, data: FloatArray) : DataMap(width, width, data) {
	val maxHeight: Float

	init {
		var min = data[0]
		for (i in data.indices) {
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
	}
}
