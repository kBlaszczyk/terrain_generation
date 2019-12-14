package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.Quad
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector3f


class TerrainSceneObject(
	width: Float, private val camera: Camera, private val shader: TerrainShader
) {
	private val quad = Quad
	private val model = Matrix4f().scale(width, 1f, width)
	private val view = Matrix4f()
	private val modelView = Matrix4f()
	private val modelViewProjection = Matrix4f()

	fun update() {
		camera.getView(view)
		modelView.set(view).mul(model)
		camera.getProjectionView(modelViewProjection).mul(model)
	}

	fun draw() {
		shader.setModel(model)
		shader.setModelView(modelView)
		shader.setModelViewProjection(modelViewProjection)
		quad.draw()
	}

	fun translate(offset: Vector3f) {
		model.translate(offset)
		modelView.set(view).mul(model)
		camera.getProjectionView(modelViewProjection).mul(model)
	}
}
