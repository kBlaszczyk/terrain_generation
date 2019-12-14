package de.orchound.rendering.opengl

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY
import org.lwjgl.opengl.GL30.glTexSubImage3D
import org.lwjgl.opengl.GL42.glTexStorage3D
import java.nio.ByteBuffer


open class OpenGLTexture(width: Int, height: Int, data: ByteBuffer) {
	val handle = glGenTextures()

	init {
		glBindTexture(GL_TEXTURE_2D, handle)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
	}
}

class OpenGLTextureArray(texturesCount: Int, width: Int, height: Int, data: ByteBuffer) {
	val handle = glGenTextures()

	init {
		glBindTexture(GL_TEXTURE_2D_ARRAY, handle)
		glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, width, height, texturesCount)
		glTexSubImage3D(
			GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0,
			width, height, texturesCount, GL_RGBA, GL_UNSIGNED_BYTE, data
		)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
	}
}
