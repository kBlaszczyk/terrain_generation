package de.orchound.rendering.shaderutility

import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class OpenGLShader(
	private val programHandle: Int, uniformProvider: UniformProvider
) : Shader {

	private val samplerSetterByUniform: Map<String, (Int) -> Unit> = uniformProvider.getUniformTypes()
		.filter { it.startsWith("sampler") }
		.flatMap { uniformProvider.getUniforms(it) }
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { value: Int -> setIntUniform(location, value) }
		}

	private val intSetterByUniform: Map<String, (Int) -> Unit> = uniformProvider.getUniforms("int")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { value: Int -> setIntUniform(location, value) }
		}

	private val floatSetterByUniform: Map<String, (Float) -> Unit> = uniformProvider.getUniforms("float")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { value: Float -> setFloatUniform(location, value) }
		}

	private val floatArraySetterByUniform: Map<String, (FloatBuffer) -> Unit> = uniformProvider.getUniforms("float[]")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { values: FloatBuffer -> setFloatBufferUniform(location, values) }
		}

	private val vec2SetterByUniform = uniformProvider.getUniforms("vec2")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { x: Float, y: Float -> setFloat2Uniform(location, x, y) }
		}

	private val vec3SetterByUniform = uniformProvider.getUniforms("vec3")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { x: Float, y: Float, z: Float -> setFloat3Uniform(location, x, y, z) }
		}

	private val vec4SetterByUniform = uniformProvider.getUniforms("vec4")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { x: Float, y: Float, z: Float, w: Float -> setFloat4Uniform(location, x, y, z, w) }
		}

	private val mat3SetterByUniform = uniformProvider.getUniforms("mat3")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { matrix: FloatBuffer -> setFloatBufferUniform(location, matrix) }
		}

	private val mat4SetterByUniform = uniformProvider.getUniforms("mat4")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { matrix: FloatBuffer -> setMat4Uniform(location, matrix) }
		}

	override fun bind() = glUseProgram(programHandle)
	override fun unbind() = glUseProgram(0)

	fun setTexture(handle: Int) {
		setTexture(handle, 0)
	}

	fun setTexture(handle: Int, target: Int) {
		require(target <= 31)

		glActiveTexture(GL_TEXTURE0 + target)
		glBindTexture(GL_TEXTURE_2D, handle)
	}

	fun getSamplerSetter(uniform: String) = samplerSetterByUniform.getOrDefault(uniform) { Unit }
	fun getIntSetter(uniform: String) = intSetterByUniform.getOrDefault(uniform) { Unit }
	fun getFloatSetter(uniform: String) = floatSetterByUniform.getOrDefault(uniform) { Unit }
	fun getFloatArraySetter(uniform: String) = floatArraySetterByUniform.getOrDefault(uniform) { Unit }

	fun getVec2Setter(uniform: String) = vec2SetterByUniform.getOrDefault(uniform) { _, _ -> Unit }
	fun getVec3Setter(uniform: String) = vec3SetterByUniform.getOrDefault(uniform) { _, _, _ ->  Unit }
	fun getVec4Setter(uniform: String) = vec4SetterByUniform.getOrDefault(uniform) { _, _, _, _ ->  Unit }

	fun getMat3Setter(uniform: String) = mat3SetterByUniform.getOrDefault(uniform) { Unit }
	fun getMat4Setter(uniform: String) = mat4SetterByUniform.getOrDefault(uniform) { Unit }

	private fun setIntUniform(uniform: Int, value: Int) = glUniform1i(uniform, value)

	private fun setFloatUniform(uniform: Int, value: Float) = glUniform1f(uniform, value)
	private fun setFloat2Uniform(uniform: Int, x: Float, y: Float) = glUniform2f(uniform, x, y)
	private fun setFloat3Uniform(uniform: Int, x: Float, y: Float, z: Float) = glUniform3f(uniform, x, y, z)
	private fun setFloat4Uniform(uniform: Int, x: Float, y: Float, z: Float, w: Float) =
		glUniform4f(uniform, x, y, z, w)
	private fun setFloatBufferUniform(uniform: Int, values: FloatBuffer) = glUniform1fv(uniform, values)

	private fun setMat3Uniform(uniform: Int, matrix: FloatBuffer) =
		glUniformMatrix3fv(uniform, false, matrix)
	private fun setMat4Uniform(uniform: Int, matrix: FloatBuffer) =
		glUniformMatrix4fv(uniform, false, matrix)
}
