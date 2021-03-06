package de.orchound.rendering.opengl

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL40.*


class TerrainShader {

	private val handle: Int

	private val modelLocation: Int
	private val modelViewLocation: Int
	private val modelViewProjectionLocation: Int
	private val csLightDirectionLocation: Int
	private val layersCountLocation: Int
	private val layerColorLocations: IntArray
	private val layerLimitLocations: IntArray
	private val layerBlendingHeightLocations: IntArray
	private val layerTexturesLocation: Int
	private val heightMapLocation: Int
	private val normalMapLocation: Int
	private val tessellationOriginLocation: Int
	private val tessellationWidthLocation: Int

	private val matrixBuffer = FloatArray(16)
	private val vec3Buffer = FloatArray(3)
	private val vec2Buffer = FloatArray(2)
	private val vec4Buffer = FloatArray(4)

	init {
		val vertexShaderSource = loadShaderSource("/shader/TerrainShader_vs.glsl")
		val fragmentShaderSource = loadShaderSource("/shader/TerrainShader_fs.glsl")
		val tessellationControlShaderSource = loadShaderSource("/shader/TerrainShader_tcs.glsl")
		val tessellationEvaluationShaderSource = loadShaderSource("/shader/TerrainShader_tes.glsl")

		handle = createShaderProgram(
			vertexShaderSource, fragmentShaderSource,
			tessellationControlShaderSource, tessellationEvaluationShaderSource
		)

		modelLocation = getUniformLocation("model")
		modelViewLocation = getUniformLocation("model_view")
		modelViewProjectionLocation = getUniformLocation("model_view_projection")
		csLightDirectionLocation = getUniformLocation("light_direction_cs")
		tessellationOriginLocation = getUniformLocation("tessellation_origin")
		tessellationWidthLocation = getUniformLocation("tessellation_width")

		layersCountLocation = getUniformLocation("layers_count")
		layerColorLocations = (0 until MAX_LAYERS).map {
			getUniformLocation("layer_colors[$it]")
		}.toIntArray()
		layerLimitLocations = (0 until MAX_LAYERS).map {
			getUniformLocation("layer_limits[$it]")
		}.toIntArray()
		layerBlendingHeightLocations = (0 until MAX_LAYERS).map {
			getUniformLocation("layer_blending_heights[$it]")
		}.toIntArray()

		layerTexturesLocation = glGetUniformLocation(handle, "layer_textures")
		heightMapLocation = glGetUniformLocation(handle, "height_map")
		normalMapLocation = glGetUniformLocation(handle, "normal_map")

		bind()
		glUniform1i(layerTexturesLocation, 0)
		glUniform1i(heightMapLocation, 1)
		glUniform1i(normalMapLocation, 2)
		unbind()
	}

	fun bind() = glUseProgram(handle)
	fun unbind() = glUseProgram(0)

	/**
	 * Sets the Uniform variable for the Model matrix.
	 * This method needs to be called after the shader has been bound.
	 */
	fun setModel(matrix: Matrix4f) = setUniformMatrix(modelLocation, matrix)
	fun setModelView(matrix: Matrix4f) = setUniformMatrix(modelViewLocation, matrix)
	fun setModelViewProjection(matrix: Matrix4f) = setUniformMatrix(modelViewProjectionLocation, matrix)
	fun setCsLightDirection(vector: Vector3f) = setUniformVector3(csLightDirectionLocation, vector)
	fun setTessellationOrigin(vector: Vector2f) = setUniformVector2(tessellationOriginLocation, vector)
	fun setTessellationWidth(width: Float) = setUniformFloat(tessellationWidthLocation, width)
	fun setLayersCount(value: Int) = glUniform1i(layersCountLocation, value)

	fun setLayerColors(colors: Array<Vector3f>) {
		for (index in colors.indices) {
			setUniformVector3(layerColorLocations[index], colors[index])
		}
	}

	fun setLayerLimits(limits: FloatArray) {
		for (index in limits.indices)
			setUniformFloat(layerLimitLocations[index], limits[index])
	}

	fun setLayerBlendingHeights(blendingHeights: FloatArray) {
		for (index in blendingHeights.indices)
			setUniformFloat(layerBlendingHeightLocations[index], blendingHeights[index])
	}

	fun setTextureArray(textureArrayHandle: Int) {
		glActiveTexture(GL_TEXTURE0)
		glBindTexture(GL_TEXTURE_2D, textureArrayHandle)
	}

	fun setHeightMap(handle: Int) {
		glActiveTexture(GL_TEXTURE1)
		glBindTexture(GL_TEXTURE_2D, handle)
	}

	fun setNormalMap(handle: Int) {
		glActiveTexture(GL_TEXTURE2)
		glBindTexture(GL_TEXTURE_2D, handle)
	}

	fun setTessellationLevels(inner: Float, outer: Float) {
		vec4Buffer.fill(inner)
		glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, vec4Buffer)
		vec2Buffer.fill(outer)
		glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, vec2Buffer)
	}

	private fun getUniformLocation(name: String) = glGetUniformLocation(handle, name)

	private fun setUniformMatrix(location: Int, matrix: Matrix4f) {
		if (location != -1) {
			matrix.get(matrixBuffer)
			glUniformMatrix4fv(location, false, matrixBuffer)
		}
	}

	private fun setUniformVector2(location: Int, vector: Vector2f) {
		if (location != -1)
			glUniform2f(location, vector.x, vector.y)
	}

	private fun setUniformVector3(location: Int, vector: Vector3f) {
		if (location != -1)
			glUniform3f(location, vector.x, vector.y, vector.z)
	}

	private fun setUniformFloat(location: Int, value: Float) {
		if (location != -1)
			glUniform1f(location, value)
	}

	private fun createShaderProgram(
		vertexShaderSource: Array<String>, fragmentShaderSource: Array<String>,
		tessellationControlShaderSource: Array<String>, tessellationEvaluationShaderSource: Array<String>
	): Int {
		val vertexShaderHandle = compileShader(vertexShaderSource, GL_VERTEX_SHADER)
		val tessellationControlShaderHandle = compileShader(tessellationControlShaderSource, GL_TESS_CONTROL_SHADER)
		val tessellationEvaluationShaderHandle = compileShader(tessellationEvaluationShaderSource, GL_TESS_EVALUATION_SHADER)
		val fragmentShaderHandle = compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER)

		val programHandle = glCreateProgram()
		glAttachShader(programHandle, vertexShaderHandle)
		glAttachShader(programHandle, tessellationControlShaderHandle)
		glAttachShader(programHandle, tessellationEvaluationShaderHandle)
		glAttachShader(programHandle, fragmentShaderHandle)

		glLinkProgram(programHandle)

		glDetachShader(programHandle, vertexShaderHandle)
		glDetachShader(programHandle, tessellationControlShaderHandle)
		glDetachShader(programHandle, tessellationEvaluationShaderHandle)
		glDetachShader(programHandle, fragmentShaderHandle)
		glDeleteShader(vertexShaderHandle)
		glDeleteShader(tessellationControlShaderHandle)
		glDeleteShader(tessellationEvaluationShaderHandle)
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

	private fun getProgramInfoLog(programId:Int): String {
		return glGetProgramInfoLog(programId, GL_INFO_LOG_LENGTH)
	}

	private fun getShaderInfoLog(shaderId:Int): String {
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

	companion object {
		const val MAX_LAYERS = 10
	}
}
