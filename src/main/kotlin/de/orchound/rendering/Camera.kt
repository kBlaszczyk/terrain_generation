package de.orchound.rendering

import de.orchound.rendering.display.Keys
import org.joml.Matrix4f
import org.joml.Vector3f


class Camera(aspectRatio: Float, fov: Float, private val boundary: Float) {

	private val position: Vector3f = Vector3f(0f, 25f, 10f)

	private val up = UP
	private val center = Vector3f(0f, 0f, 0f)
	private val speed = 10f

	private var yaw = 0f
	private var pitch = 0f

	private val fovY = toRadians(fov / aspectRatio)
	private val nearPlane = 0.5f
	private val farPlane = 1000f

	private var mouseSensitivity = 0.1
	private var mousePosX = 0.0
	private var mousePosY = 0.0
	private var mouseDeltaX = 0.0
	private var mouseDeltaY = 0.0

	private val viewMatrix = Matrix4f().setLookAt(position, center, up)
	private val projectionMatrix = Matrix4f()
		.setPerspective(fovY, aspectRatio, nearPlane, farPlane)

	fun getView(dest: Matrix4f): Matrix4f = dest.set(viewMatrix)
	fun getViewProjection(dest: Matrix4f): Matrix4f = projectionMatrix.mul(viewMatrix, dest)

	fun update() {
		updateMousePosition()

		val direction = getMovementDirection(Vector3f())
		if (direction.lengthSquared() > 0.1) {
			viewMatrix.transpose().transformDirection(direction.normalize())
			move(direction)
		}

		yaw += (-mouseDeltaX * mouseSensitivity).toFloat()
		pitch += (-mouseDeltaY * mouseSensitivity).toFloat()
		clampPitch()

		updateView()
	}

	fun getPosition(dest: Vector3f): Vector3f = dest.set(position)

	private fun updateMousePosition() {
		val newMousePosX = Window.mousePosX
		val newMousePosY = Window.mousePosY
		mouseDeltaX = mousePosX - newMousePosX
		mouseDeltaY = mousePosY - newMousePosY
		mousePosX = newMousePosX
		mousePosY = newMousePosY
	}

	private fun updateView() {
		viewMatrix.identity()
			.rotateX(toRadians(pitch))
			.rotateY(toRadians(yaw))
			.translate(-position.x, -position.y, -position.z)
	}

	private fun getMovementDirection(dest: Vector3f): Vector3f {
		dest.zero()
		if (Window.getPressedKeys().contains(Keys.A))
			dest.add(LEFT)
		if (Window.getPressedKeys().contains(Keys.D))
			dest.add(RIGHT)
		if (Window.getPressedKeys().contains(Keys.W))
			dest.add(FORWARD)
		if (Window.getPressedKeys().contains(Keys.S))
			dest.add(BACK)

		return dest
	}

	private fun move(direction: Vector3f) {
		position.add(direction.mul(speed * Time.deltaTime))

		val halfBoundary = boundary / 2
		if (position.x > halfBoundary)
			position.x -= boundary
		else if (position.x < -halfBoundary)
			position.x += boundary
		if (position.z > halfBoundary)
			position.z -= boundary
		else if (position.z < -halfBoundary)
			position.z += boundary
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
