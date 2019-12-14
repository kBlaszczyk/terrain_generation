package de.orchound.rendering.opengl

import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.opengl.GL40.*


object Quad {
	private val vao = glGenVertexArrays()

	init {
		val vertices = floatArrayOf(-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f)

		val vbo = glGenBuffers()
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
		glBindBuffer(GL_ARRAY_BUFFER, 0)

		glBindVertexArray(vao)
		glEnableVertexAttribArray(0)
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
		glBindVertexArray(0)
	}

	fun draw() {
		glPatchParameteri(GL_PATCH_VERTICES, 4)
		glBindVertexArray(vao)
		glDrawArrays(GL_PATCHES, 0, 4)
		glBindVertexArray(0)
	}
}
