package de.orchound.rendering.opengl

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*


class TerrainShader {

	private val handle: Int

	private val modelViewLocation: Int
	private val modelViewProjectionLocation: Int
	private val textureLocation: Int

	private val matrixBuffer = FloatArray(16)

	init {
		val vertexShaderSource = loadShaderSource("/shader/TerrainShader_vs.glsl")
		val fragmentShaderSource = loadShaderSource("/shader/TerrainShader_fs.glsl")

		handle = createShaderProgram(vertexShaderSource, fragmentShaderSource)

		modelViewLocation = getUniformLocation("model_view")
		modelViewProjectionLocation = getUniformLocation("model_view_projection")
		textureLocation = glGetUniformLocation(handle, "texture_sampler")
		glUniform1i(textureLocation, 0)
	}

	fun bind() = glUseProgram(handle)
	fun unbind() = glUseProgram(0)

	/**
	 * Sets the Uniform variable for the Model matrix.
	 * This method needs to be called after the shader has been bound.
	 */
	fun setModelView(matrix: Matrix4f) = setUniformMatrix(modelViewLocation, matrix)
	fun setModelViewProjection(matrix: Matrix4f) = setUniformMatrix(modelViewProjectionLocation, matrix)

	fun setTexture(textureHandle: Int) {
		glActiveTexture(GL_TEXTURE0)
		glBindTexture(GL_TEXTURE_2D, textureHandle)
	}

	private fun getUniformLocation(name: String) = glGetUniformLocation(handle, name)

	private fun setUniformMatrix(location: Int, matrix: Matrix4f) {
		if (location != -1) {
			matrix.get(matrixBuffer)
			glUniformMatrix4fv(location, false, matrixBuffer)
		}
	}

	private fun createShaderProgram(vertexShaderSource: Array<String>, fragmentShaderSource: Array<String>): Int {
		val vertexShaderHandle = compileShader(vertexShaderSource, GL_VERTEX_SHADER)
		val fragmentShaderHandle = compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER)

		val programHandle = glCreateProgram()
		glAttachShader(programHandle, vertexShaderHandle)
		glAttachShader(programHandle, fragmentShaderHandle)

		glBindAttribLocation(programHandle, 0, "in_position")
		glBindAttribLocation(programHandle, 1, "in_normal")
		glBindAttribLocation(programHandle, 2, "in_texcoords")

		glLinkProgram(programHandle)

		glDetachShader(programHandle, vertexShaderHandle)
		glDetachShader(programHandle, fragmentShaderHandle)
		glDeleteShader(vertexShaderHandle)
		glDeleteShader(fragmentShaderHandle)

		validateShaderLinking(programHandle)
		validateShaderProgram(programHandle)

		return programHandle
	}

	private fun compileShader(shaderSource: Array<String>, type: Int): Int {
		val shaderId = glCreateShader(type)

		glShaderSource(shaderId, *shaderSource)
		glCompileShader(shaderId)

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			val info = getShaderInfoLog(shaderId)
			val shaderType = if (type == GL_VERTEX_SHADER) "Vertex" else "Fragment"
			throw Exception("$shaderType shader compilation failed: $info")
		}

		return shaderId
	}

	private fun getProgramInfoLog(programId:Int):String {
		return glGetProgramInfoLog(programId, GL_INFO_LOG_LENGTH)
	}

	private fun getShaderInfoLog(shaderId:Int):String {
		return glGetShaderInfoLog(shaderId, GL_INFO_LOG_LENGTH)
	}

	private fun validateShaderProgram(programId:Int) {
		glValidateProgram(programId)

		val error = glGetError()
		if (error != 0)
			throw Exception("OpenGL shader creation failed. Error code:$error")
	}

	private fun validateShaderLinking(programId: Int) {
		if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
			val info = getProgramInfoLog(programId)
			throw Exception("OpenGL shader linking failed: $info")
		}
	}

	private fun loadShaderSource(resource: String): Array<String> {
		return javaClass.getResourceAsStream(resource).use { inputStream ->
			inputStream.bufferedReader().readLines()
		}.map { "$it\n" }.toTypedArray()
	}
}
