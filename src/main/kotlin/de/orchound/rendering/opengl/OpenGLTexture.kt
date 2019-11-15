package de.orchound.rendering.opengl

import org.lwjgl.opengl.GL11.*
import java.nio.ByteBuffer


open class OpenGLTexture(width: Int, height: Int, data: ByteBuffer) {
	val handle = glGenTextures()

	init {
		glBindTexture(GL_TEXTURE_2D, handle)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data)
	}
}
