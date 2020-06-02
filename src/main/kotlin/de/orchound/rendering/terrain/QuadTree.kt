package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import org.joml.Matrix4f
import org.joml.Vector2f


class QuadTree(
	val width: Float, drawDeterminer: DrawDeterminer, model: Matrix4f, camera: Camera, material: TerrainMaterial
) {
	private val root = QuadNode(drawDeterminer, camera, material, Vector2f(0f), width, 0, width);
	fun draw() = root.draw()
}
