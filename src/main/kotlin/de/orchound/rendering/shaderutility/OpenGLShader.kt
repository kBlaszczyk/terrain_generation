package de.orchound.rendering.shaderutility

import org.joml.*
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class OpenGLShader(
	private val programHandle: Int, uniformProvider: UniformProvider
) : Shader {

	private val intSetterByUniform: Map<String, (Int) -> Unit> = uniformProvider.getUniforms("int")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { value: Int -> setIntUniform(location, value) }
		}

	private val samplerSetterByUniform: Map<String, (Int) -> Unit> = uniformProvider.getUniformTypes()
		.filter { it.startsWith("sampler") }
		.flatMap { uniformProvider.getUniforms(it) }
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
			uniform to { values: FloatBuffer -> setFloatArrayUniform(location, values) }
		}

	private val vec2SetterByUniform: Map<String, (Vector2f) -> Unit> = uniformProvider.getUniforms("vec2")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { vector: Vector2f -> setVec2Uniform(location, vector) }
		}

	private val vec3SetterByUniform: Map<String, (Vector3f) -> Unit> = uniformProvider.getUniforms("vec3")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { vector: Vector3f -> setVec3Uniform(location, vector) }
		}

	private val vec4SetterByUniform: Map<String, (Vector4f) -> Unit> = uniformProvider.getUniforms("vec4")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { vector: Vector4f -> setVec4Uniform(location, vector) }
		}

	private val mat3SetterByUniform: Map<String, (Matrix3f) -> Unit> = uniformProvider.getUniforms("mat3")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { matrix: Matrix3f -> setMat3Uniform(location, matrix) }
		}

	private val mat4SetterByUniform: Map<String, (Matrix4f) -> Unit> = uniformProvider.getUniforms("mat4")
		.map { uniform -> uniform to glGetUniformLocation(programHandle, uniform) }
		.filterNot { (_, location) -> location == -1 }
		.associate { (uniform, location) ->
			uniform to { matrix: Matrix4f -> setMat4Uniform(location, matrix) }
		}

	private val matrix3Buffer = FloatArray(9)
	private val matrix4Buffer = FloatArray(16)

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

	fun getIntSetter(uniform: String): (Int) -> Unit =
		intSetterByUniform.getOrDefault(uniform) { Unit }
	fun getFloatSetter(uniform: String): (Float) -> Unit =
		floatSetterByUniform.getOrDefault(uniform) { Unit }
	fun getFloatArraySetter(uniform: String): (FloatBuffer) -> Unit =
		floatArraySetterByUniform.getOrDefault(uniform) { Unit }
	fun getVector2Setter(uniform: String): (Vector2f) -> Unit =
		vec2SetterByUniform.getOrDefault(uniform) { Unit }
	fun getVector3Setter(uniform: String): (Vector3f) -> Unit =
		vec3SetterByUniform.getOrDefault(uniform) { Unit }
	fun getVector4Setter(uniform: String): (Vector4f) -> Unit =
		vec4SetterByUniform.getOrDefault(uniform) { Unit }
	fun getMatrix3Setter(uniform: String): (Matrix3f) -> Unit =
		mat3SetterByUniform.getOrDefault(uniform) { Unit }
	fun getMatrix4Setter(uniform: String): (Matrix4f) -> Unit =
		mat4SetterByUniform.getOrDefault(uniform) { Unit }
	fun getSamplerSetter(uniform: String): (Int) -> Unit =
		samplerSetterByUniform.getOrDefault(uniform) { Unit }

	private fun setIntUniform(uniform: Int, value: Int) = glUniform1i(uniform, value)
	private fun setFloatUniform(uniform: Int, value: Float) = glUniform1f(uniform, value)
	private fun setVec2Uniform(uniform: Int, value: Vector2f) = glUniform2f(uniform, value.x, value.y)
	private fun setVec3Uniform(uniform: Int, value: Vector3f) = glUniform3f(uniform, value.x, value.y, value.z)
	private fun setVec4Uniform(uniform: Int, value: Vector4f) = glUniform4f(uniform, value.x, value.y, value.z, value.w)

	private fun setFloatArrayUniform(uniform: Int, values: FloatBuffer) = glUniform1fv(uniform, values)
	private fun setVector3ArrayUniform(uniform: Int, vectors: FloatBuffer) = glUniform3fv(uniform, vectors)

	private fun setMat3Uniform(uniform: Int, matrix: Matrix3f) {
		matrix.get(matrix3Buffer)
		glUniformMatrix3fv(uniform, false, matrix3Buffer)
	}

	private fun setMat4Uniform(uniform: Int, matrix: Matrix4f) {
		matrix.get(matrix4Buffer)
		glUniformMatrix4fv(uniform, false, matrix4Buffer)
	}
}
