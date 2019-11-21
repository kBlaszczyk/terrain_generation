package de.orchound.rendering.opengl

import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


object TextureLoader {

	fun loadTexture(resource: String): OpenGLTexture {
		val file = File(javaClass.getResource(resource).toURI())

		return MemoryStack.stackPush().use { frame ->
			val byteBuffer = file.inputStream().use { inputStream ->
				inputStream.channel.use { fileChannel ->
					fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
				}
			}

			val width = frame.mallocInt(1)
			val height = frame.mallocInt(1)
			val components = frame.mallocInt(1)

			val data = STBImage.stbi_load_from_memory(byteBuffer, width, height, components, 4) ?:
			throw Exception("Failed to load image data for file: $file")

			val texture = OpenGLTexture(width.get(), height.get(), data)
			STBImage.stbi_image_free(data)
			texture
		}
	}

	fun loadTextureArray(vararg resources: String): OpenGLTextureArray {
		var texturesData = ByteArray(0)
		var requiredSize = Pair(0, 0)

		for (resource in resources) {
			val bytes = javaClass.getResourceAsStream(resource).use { inputStream ->
				inputStream.readBytes()
			}

			MemoryStack.stackPush().use { frame ->
				val width = frame.mallocInt(1)
				val height = frame.mallocInt(1)
				val components = frame.mallocInt(1)

				val byteBuffer = ByteBuffer.allocateDirect(bytes.size)
				byteBuffer.put(bytes).flip()
				val data = STBImage.stbi_load_from_memory(
					byteBuffer, width, height, components, 4
				) ?: throw Exception("Failed to load image data for file: $resource")

				if (requiredSize.first == 0 || requiredSize.second == 0)
					requiredSize = Pair(width.get(), height.get())
				else if (width.get() != requiredSize.first || height.get() != requiredSize.second)
					throw Exception("All textures inside a texture array need to have the same width and height values.")

				val textureData = ByteArray(data.remaining())
				data.get(textureData).flip()
				STBImage.stbi_image_free(data)

				texturesData += textureData
			}
		}

		val byteBuffer = ByteBuffer.allocateDirect(texturesData.size)
		byteBuffer.put(texturesData).flip()
		return OpenGLTextureArray(resources.size, requiredSize.first, requiredSize.second, byteBuffer)
	}
}
