package de.orchound.rendering

import de.orchound.rendering.display.GLFWKeyMapper
import de.orchound.rendering.display.Keys
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import java.util.*


object Window {

	val width = 1280
	val height = 720
	val aspectRatio = width.toFloat() / height

	var mousePosX = 0.0
		private set
	var mousePosY = 0.0
		private set

	var title: String = "Terrain Generation"
		set(value) {
			glfwSetWindowTitle(handle, value)
			field = value
		}

	private var handle: Long = 0

	private val pressedKeys = HashSet<Keys>()

	private var errorCallback = GLFWErrorCallback.createPrint(System.err)
	private var framebufferSizeCallback = GLFWFramebufferSizeCallback.create { window, newWidth, newHeight ->
		if (window == handle)
			glViewport(0, 0, newWidth, newHeight)
	}

	private val keyCallback = GLFWKeyCallback.create { _, key, scancode, action, _ ->
		when (action) {
			GLFW_PRESS -> pressedKeys.add(GLFWKeyMapper[key])
			GLFW_RELEASE -> pressedKeys.remove(GLFWKeyMapper[key])
		}
	}
	private val mouseButtonCallback = GLFWMouseButtonCallback.create { _, button, action, _ ->
		when (action) {
			GLFW_PRESS -> pressedKeys.add(GLFWKeyMapper[button])
			GLFW_RELEASE -> pressedKeys.remove(GLFWKeyMapper[button])
		}
	}
	private val mousePositionCallback = GLFWCursorPosCallback.create { _, xPos, yPos ->
		mousePosX = xPos
		mousePosY = yPos
	}

	fun initialize() {
		if (!glfwInit())
			throw RuntimeException("Failed to initialize GLFW")

		glfwSetErrorCallback(errorCallback)

		glfwDefaultWindowHints()
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0)
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

		handle = glfwCreateWindow(width, height, title, 0, 0)
		if (handle == 0L)
			throw Exception("Failed to create window")

		glfwSetFramebufferSizeCallback(handle, framebufferSizeCallback)
		glfwSetKeyCallback(handle, keyCallback)
		glfwSetMouseButtonCallback(handle, mouseButtonCallback)
		glfwSetCursorPosCallback(handle, mousePositionCallback)
		//glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwMakeContextCurrent(handle)
		GL.createCapabilities()
		initRendering()
	}

	fun getPressedKeys(): Set<Keys> = pressedKeys
	fun shouldClose() = glfwWindowShouldClose(handle)

	fun prepareFrame() {
		glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
	}

	fun finishFrame() {
		glfwSwapBuffers(handle)
		glfwPollEvents()
	}

	fun destroy() {
		keyCallback.close()
		mouseButtonCallback.close()
		mousePositionCallback.close()
		errorCallback.close()
		framebufferSizeCallback.close()

		glfwDestroyWindow(handle)
		glfwTerminate()
	}

	private fun initRendering() {
		glClearColor(0.71f, 0.86f, 0.94f, 1f)
		//glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
		glEnable(GL_DEPTH_TEST)
		glEnable(GL_CULL_FACE)
		glCullFace(GL_BACK)
	}
}
