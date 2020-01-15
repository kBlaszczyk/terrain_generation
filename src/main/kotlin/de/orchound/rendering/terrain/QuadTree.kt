package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector2f


class QuadTree(
	val width: Float, drawDeterminer: DrawDeterminer, model: Matrix4f, camera: Camera, shader: TerrainShader
) {
	private val root = QuadNode(drawDeterminer, camera, shader, Vector2f(0f), width, 0, width);
	fun draw() = root.draw()
}
