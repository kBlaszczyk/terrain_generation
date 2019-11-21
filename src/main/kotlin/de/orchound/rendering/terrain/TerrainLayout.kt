package de.orchound.rendering.terrain

import de.orchound.rendering.Color
import org.joml.Vector3f


class TerrainLayout {

	private data class TerrainLayer(val name: String, val color: Color, val upperLimit: Float, val blendingHeight: Float)

	private val layers: MutableCollection<TerrainLayer> = ArrayList()

	fun addLayer(name: String, color: Color, upperLimit: Float, blendingHeight: Float) {
		layers.add(TerrainLayer(name, color, upperLimit, blendingHeight))
	}

	fun getColor(heightValue: Float): Color {
		val terrainLayer = layers.find { it.upperLimit > heightValue }
			?: layers.last()

		return terrainLayer.color
	}

	fun getColors(dest: Array<Vector3f>) {
		val offset = 10 - layers.size
		layers.withIndex().forEach {
			it.value.color.toRgbVector(dest[it.index + offset])
		}
	}

	fun getLimits(dest: FloatArray) {
		val offset = 10 - layers.size
		layers.withIndex().forEach {
			dest[it.index + offset] = it.value.upperLimit
		}
	}

	fun getBlendingHeights(dest: FloatArray) {
		val offset = 10 - layers.size
		layers.withIndex().forEach {
			dest[it.index + offset] = it.value.blendingHeight
		}
	}
}
