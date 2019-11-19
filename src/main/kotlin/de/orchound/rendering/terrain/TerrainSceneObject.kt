package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.OpenGLMesh
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector3f


class TerrainSceneObject(
	private val mesh: OpenGLMesh, private val camera: Camera, private val shader: TerrainShader
) {
	private val model = Matrix4f()
	private val view = Matrix4f()
	private val modelView = Matrix4f()
	private val modelViewProjection = Matrix4f()

	fun update() {
		camera.getView(view)
		modelView.set(view).mul(model)
		camera.getProjectionView(modelViewProjection).mul(model)
	}

	fun draw() {
		shader.setModelView(modelView)
		shader.setModelViewProjection(modelViewProjection)
		mesh.draw()
	}

	fun translation(offset: Vector3f) {
		model.translation(offset)
		modelView.set(view).mul(model)
		camera.getProjectionView(modelViewProjection).mul(model)
	}
}
