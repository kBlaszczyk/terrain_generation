package de.orchound.rendering.terrain

class NoiseMap(val width: Int, val data: FloatArray) {

	init {
		require(data.size == width * width)
	}

	fun getValue(x: Int, y: Int): Float {
		return data[y * width + x]
	}
}
