package de.orchound.rendering.terrain

import de.orchound.rendering.Camera
import org.joml.Vector3f
import kotlin.math.abs
import kotlin.math.max


class DrawDeterminer(private val camera: Camera) {

	private val cameraPosition = Vector3f()

	fun determineNodeRendering(quadNode: QuadNode): Boolean {
		val halfWidth = quadNode.width / 2f
		val dx = max(abs(cameraPosition.x - quadNode.translation.x) - halfWidth, 0f)
		val dy = max(abs(cameraPosition.z - quadNode.translation.y) - halfWidth, 0f)
		val squaredDistance = dx * dx + dy * dy

		return squaredDistance > quadNode.width * quadNode.width
	}

	fun update() {
		camera.getPosition(cameraPosition)
	}
}
