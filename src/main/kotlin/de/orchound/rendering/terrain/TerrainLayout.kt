package de.orchound.rendering.terrain

import de.orchound.rendering.Color


class TerrainLayout {

	private data class TerrainLayer(val name: String, val upperLimit: Float, val color: Color)

	private val layers: MutableCollection<TerrainLayer> = ArrayList()

	fun addLayer(name: String, upperLimit: Float, color: Color) {
		layers.add(TerrainLayer(name, upperLimit, color))
	}

	fun getColor(heightValue: Float): Color {
		val terrainLayer = layers.find { it.upperLimit > heightValue }
			?: layers.last()

		return terrainLayer.color
	}
}
