package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.Quad
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector2f


class QuadNode(
	val translation: Vector2f, val width: Float, level: Int, private val drawDeterminer: DrawDeterminer,
	val camera: Camera, val shader: TerrainShader
) {

	private val children: MutableList<QuadNode> = ArrayList()

	private val model = Matrix4f().translate(translation.x, 0f, translation.y).scale(width, 1f, width)
	private val modelView = Matrix4f()
	private val modelViewProjection = Matrix4f()

	init {
		if (width > 4f) {
			for (i in 0 .. 3) {
				val baseOffset = width / 4
				val xOffset = if (i == 1 || i == 3) baseOffset else -baseOffset
				val yOffset = if (i == 0 || i == 1) baseOffset else -baseOffset
				val child = QuadNode(
					translation.add(xOffset, yOffset, Vector2f()), width / 2, level + 1,
					drawDeterminer, camera, shader
				)
				children.add(child)
			}
		}
	}

	fun draw() {
		if (children.isEmpty() || drawDeterminer.determineNodeRendering(this)) {
			camera.getView(modelView).mul(model)
			camera.getViewProjection(modelViewProjection).mul(model)

			shader.setModel(model)
			shader.setModelView(modelView)
			shader.setModelViewProjection(modelViewProjection)
			Quad.draw()
		} else {
			children.forEach(QuadNode::draw)
		}
	}
}
