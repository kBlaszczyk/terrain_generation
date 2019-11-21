package de.orchound.rendering.terrain

import de.orchound.rendering.Color
import org.joml.Vector3f


class TerrainLayout {
	var layersCount = 0
		private set

	private data class TerrainLayer(
		val name: String, val color: Color, val upperLimit: Float, val blendingHeight: Float
	)

	private val layers: MutableCollection<TerrainLayer> = ArrayList()

	fun addLayer(name: String, color: Color, upperLimit: Float, blendingHeight: Float) {
		layers.add(TerrainLayer(name, color, upperLimit, blendingHeight))
		layersCount++
	}

	fun getColor(heightValue: Float): Color {
		val terrainLayer = layers.find { it.upperLimit > heightValue }
			?: layers.last()

		return terrainLayer.color
	}

	fun getColors(dest: Array<Vector3f>) {
		layers.withIndex().forEach {
			it.value.color.toRgbVector(dest[it.index])
		}
	}

	fun getLimits(dest: FloatArray) {
		layers.withIndex().forEach {
			dest[it.index] = it.value.upperLimit
		}
	}

	fun getBlendingHeights(dest: FloatArray) {
		layers.withIndex().forEach {
			dest[it.index] = it.value.blendingHeight
		}
	}
}
