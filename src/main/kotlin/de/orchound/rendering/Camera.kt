package de.orchound.rendering

import de.orchound.rendering.display.Keys
import org.joml.Matrix4f
import org.joml.Vector3f


class Camera(aspectRatio: Float, fov: Float) {

	private val up = UP
	private val center = Vector3f(0f, 0f, 0f)
	private val position = Vector3f(0f, 10f, 10f)
	private var yaw = 0f
	private var pitch = 0f

	private val fovY = toRadians(fov / aspectRatio)
	private val nearPlane = 0.5f
	private val farPlane = 250f

	private var mouseSensitivity = 0.1
	private var mousePosX = 0.0
	private var mousePosY = 0.0
	private var mouseDeltaX = 0.0
	private var mouseDeltaY = 0.0

	private val viewMatrix = Matrix4f().setLookAt(position, center, up)
	private val projectionMatrix = Matrix4f()
		.setPerspective(fovY, aspectRatio, nearPlane, farPlane)

	fun getView(dest: Matrix4f): Matrix4f = dest.set(viewMatrix)
	fun getProjectionView(dest: Matrix4f): Matrix4f = projectionMatrix.mul(viewMatrix, dest)

	fun update() {
		updateMousePosition()
		updatePosition()
		updateOrientation()
	}

	private fun updateMousePosition() {
		val newMousePosX = Window.mousePosX
		val newMousePosY = Window.mousePosY
		mouseDeltaX = mousePosX - newMousePosX
		mouseDeltaY = mousePosY - newMousePosY
		mousePosX = newMousePosX
		mousePosY = newMousePosY
	}

	private fun updatePosition() {
		val movementDirection = Vector3f()
		if (Window.getPressedKeys().contains(Keys.A))
			movementDirection.add(LEFT)
		if (Window.getPressedKeys().contains(Keys.D))
			movementDirection.add(RIGHT)
		if (Window.getPressedKeys().contains(Keys.W))
			movementDirection.add(FORWARD)
		if (Window.getPressedKeys().contains(Keys.S))
			movementDirection.add(BACK)

		if (movementDirection.lengthSquared() > 0.1) {
			movementDirection.normalize().mul(0.2f)
			viewMatrix.transpose().transformDirection(movementDirection)
			position.add(movementDirection)
		}
	}

	private fun updateOrientation() {
		yaw += (-mouseDeltaX * mouseSensitivity).toFloat()
		pitch += (-mouseDeltaY * mouseSensitivity).toFloat()
		clampPitch()

		viewMatrix.identity()
			.rotateX(toRadians(pitch))
			.rotateY(toRadians(yaw))
			.translate(-position.x, -position.y, -position.z)
	}

	/**
	 * Ensures the camera's pitch isn't too high or too low.
	 */
	private fun clampPitch() {
		if (pitch < MIN_PITCH) {
			pitch = MIN_PITCH
		} else if (pitch > MAX_PITCH) {
			pitch = MAX_PITCH
		}
	}

	companion object {
		private val FORWARD = Vector3f(0f, 0f, -1f)
		private val BACK = Vector3f(0f, 0f, 1f)
		private val LEFT = Vector3f(-1f, 0f, 0f)
		private val RIGHT = Vector3f(1f, 0f, 0f)
		private val UP = Vector3f(0f, 1f, 0f)
		private val DOWN = Vector3f(0f, -1f, 0f)

		private val MAX_PITCH = 90f
		private val MIN_PITCH = -90f
	}
}
