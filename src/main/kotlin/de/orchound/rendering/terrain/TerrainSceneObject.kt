package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector3f


class TerrainSceneObject(
	width: Float, private val camera: Camera, private val shader: TerrainShader
) {
	private val model = Matrix4f().scale(width, 1f, width)
	private val view = Matrix4f()
	private val modelView = Matrix4f()
	private val modelViewProjection = Matrix4f()
	private val drawDeterminer = DrawDeterminer(camera)
	private val quadTree = QuadTree(width, drawDeterminer, model, camera, shader)

	fun update() {
		drawDeterminer.update()
	}

	fun draw() {
		quadTree.draw()
	}

	fun translate(offset: Vector3f) {
		model.translate(offset)
		modelView.set(view).mul(model)
		camera.getViewProjection(modelViewProjection).mul(model)
	}
}
