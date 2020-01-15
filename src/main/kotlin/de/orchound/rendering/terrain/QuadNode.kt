package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import de.orchound.rendering.opengl.Quad
import de.orchound.rendering.opengl.TerrainShader
import org.joml.Matrix4f
import org.joml.Vector2f


class QuadNode(
	private val drawDeterminer: DrawDeterminer, val camera: Camera, val shader: TerrainShader,
	val translation: Vector2f, val width: Float, level: Int, terrainWidth: Float
) {

	private val children: MutableList<QuadNode> = ArrayList()

	private val model = Matrix4f().translate(translation.x, 0f, translation.y).scale(width, 1f, width)
	private val modelView = Matrix4f()
	private val modelViewProjection = Matrix4f()

	private val tessellationOrigin = Vector2f()
	private val tessellationWidth = width / terrainWidth

	init {
		val halfWidth = width / 2f
		val halfTerrainWidth = terrainWidth / 2f
		tessellationOrigin.set(translation)
			.sub(halfWidth, halfWidth)
			.add(halfTerrainWidth, halfTerrainWidth)
			.mul(1 / terrainWidth)

		if (width > 4f) {
			for (i in 0 .. 3) {
				val baseOffset = width / 4
				val xOffset = if (i == 1 || i == 3) baseOffset else -baseOffset
				val yOffset = if (i == 0 || i == 1) baseOffset else -baseOffset
				val child = QuadNode(
					drawDeterminer, camera, shader, translation.add(xOffset, yOffset, Vector2f()),
					width / 2, level + 1, terrainWidth
				)
				children.add(child)
			}
		}
	}

	fun draw() {
		if (children.isEmpty() || drawDeterminer.determineNodeRendering(this)) {
			camera.getView(modelView).mul(model)
			camera.getViewProjection(modelViewProjection).mul(model)

			shader.setTessellationOrigin(tessellationOrigin)
			shader.setTessellationWidth(tessellationWidth)
			shader.setModel(model)
			shader.setModelView(modelView)
			shader.setModelViewProjection(modelViewProjection)
			Quad.draw()
		} else {
			children.forEach(QuadNode::draw)
		}
	}
}
