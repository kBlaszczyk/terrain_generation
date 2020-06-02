package de.orchound.rendering.shaderutility

import org.joml.*
import org.lwjgl.BufferUtils

class UniformJomlHelper(private val shader: OpenGLShader) {

	private val matrixBuffer = BufferUtils.createFloatBuffer(16)

	fun getVec2Setter(uniform: String): (Vector2f) -> Unit {
		val setter = shader.getVec2Setter(uniform)
		return { vector: Vector2f -> setter(vector.x, vector.y) }
	}

	fun getVec3Setter(uniform: String): (Vector3f) -> Unit {
		val setter = shader.getVec3Setter(uniform)
		return { vector: Vector3f -> setter(vector.x, vector.y, vector.z) }
	}

	fun getVec4Setter(uniform: String): (Vector4f) -> Unit {
		val setter = shader.getVec4Setter(uniform)
		return { vector: Vector4f -> setter(vector.x, vector.y, vector.z, vector.w) }
	}

	fun getMat3Setter(uniform: String): (Matrix3f) -> Unit {
		val setter = shader.getMat3Setter(uniform)
		return { matrix ->
			this.matrixBuffer.clear()
			matrix.get(this.matrixBuffer)
			setter(this.matrixBuffer)
		}
	}

	fun getMat4Setter(uniform: String): (Matrix4f) -> Unit {
		val setter = shader.getMat4Setter(uniform)
		return { matrix ->
			this.matrixBuffer.clear()
			matrix.get(this.matrixBuffer)
			setter(this.matrixBuffer)
		}
	}
}
