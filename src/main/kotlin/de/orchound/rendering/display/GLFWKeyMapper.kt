package de.orchound.rendering.display;

import org.lwjgl.glfw.GLFW.*


object GLFWKeyMapper {

	private val keyMapping = mapOf(
		Pair(GLFW_KEY_W, Keys.W),
		Pair(GLFW_KEY_A, Keys.A),
		Pair(GLFW_KEY_S, Keys.S),
		Pair(GLFW_KEY_D, Keys.D),
		Pair(GLFW_KEY_G, Keys.G),
		Pair(GLFW_KEY_X, Keys.X),
		Pair(GLFW_KEY_Y, Keys.Y),
		Pair(GLFW_KEY_Z, Keys.Z),
		Pair(GLFW_KEY_C, Keys.C),
		Pair(GLFW_KEY_Q, Keys.Q),
		Pair(GLFW_KEY_LEFT, Keys.LEFT),
		Pair(GLFW_KEY_RIGHT, Keys.RIGHT),
		Pair(GLFW_KEY_UP, Keys.UP),
		Pair(GLFW_KEY_DOWN, Keys.DOWN),
		Pair(GLFW_KEY_SPACE, Keys.SPACE),
		Pair(GLFW_KEY_ESCAPE, Keys.ESCAPE),
		Pair(GLFW_MOUSE_BUTTON_1, Keys.MOUSE_1),
		Pair(GLFW_MOUSE_BUTTON_2, Keys.MOUSE_2),
		Pair(GLFW_KEY_UNKNOWN, Keys.UNKNOWN)
	)

	operator fun get(glfwKeyCode: Int) = keyMapping[glfwKeyCode] ?: Keys.UNKNOWN
}
