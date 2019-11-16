package de.orchound.rendering.opengl;

import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer


class OpenGLMesh {

	private val vao = glGenVertexArrays()
	private var indicesCount: Int = 0
	private var indexType = OpenGLType.UNSIGNED_INT
	private var drawMode = GL_TRIANGLES

	fun setVertexAttribute(
		data: ByteBuffer, shaderLocation: Int, type: OpenGLType,
		attributeComponentsCount: Int, normalized: Boolean
	) {
		val vbo = glGenBuffers()
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)
		glBindBuffer(GL_ARRAY_BUFFER, 0)

		glBindVertexArray(vao)
		glBindBuffer(GL_ARRAY_BUFFER, vbo)
		if (!normalized && type.isIntegral)
			glVertexAttribIPointer(shaderLocation, attributeComponentsCount, type.value, 0, 0)
		else
			glVertexAttribPointer(shaderLocation, attributeComponentsCount, type.value, normalized, 0, 0)

		glEnableVertexAttribArray(shaderLocation)
		glBindVertexArray(0)
	}

	fun setIndices(indices: ByteBuffer, type: OpenGLType) {
		indicesCount = indices.remaining() / type.size
		indexType = type

		val ebo = glGenBuffers()
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

		glBindVertexArray(vao)
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
		glBindVertexArray(0)
	}

	fun draw() {
		glBindVertexArray(vao)
		glDrawElements(drawMode, indicesCount, indexType.value, 0)
		glBindVertexArray(0)
	}
}
