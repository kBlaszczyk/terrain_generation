package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.OpenGLMesh
import de.orchound.rendering.opengl.OpenGLTexture
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector3f


class TerrainSceneObject(val mesh: OpenGLMesh, val texture: OpenGLTexture) {

	private val lightDirection = Vector3f(-10f)
	private val csLightDirection = Vector3f()

	private val model = Matrix4f()
	private val view = Matrix4f()
	private val modelView = Matrix4f()
	private val modelViewProjection = Matrix4f()

	fun preparePerspective(camera: Camera) {
		camera.getView(view)
		modelView.set(view).mul(model)
		camera.getProjectionView(modelViewProjection).mul(model)
	}

	fun update() {
		view.transformDirection(lightDirection, csLightDirection).normalize()
	}

	fun prepareShader(shader: TerrainShader) {
		shader.setModelView(modelView)
		shader.setModelViewProjection(modelViewProjection)
		shader.setCsLightDirection(csLightDirection)
		shader.setTexture(texture.handle)
	}

	fun draw() = mesh.draw()
}
