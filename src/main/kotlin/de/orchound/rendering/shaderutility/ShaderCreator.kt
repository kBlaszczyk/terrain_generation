package de.orchound.rendering.shaderutility

import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER

class ShaderCreator {

	fun createShader(sourceBundle: ShaderSourceBundle): OpenGLShader {
		val vertexShaderHandle = compileShader(sourceBundle.vertexShader, GL_VERTEX_SHADER)
		val fragmentShaderHandle = compileShader(sourceBundle.fragmentShader, GL_FRAGMENT_SHADER)
		val geometryShaderHandle = sourceBundle.geometryShader?.let { compileShader(it, GL_GEOMETRY_SHADER) }
		val tessellationControlShaderHandle = sourceBundle.tessellationControlShader?.let {
			compileShader(it, GL_TESS_CONTROL_SHADER)
		}
		val tessellationEvaluationShaderHandle = sourceBundle.tessellationEvaluationShader?.let {
			compileShader(it, GL_TESS_EVALUATION_SHADER)
		}

		val programHandle = glCreateProgram()

		glAttachShader(programHandle, vertexShaderHandle)
		glAttachShader(programHandle, fragmentShaderHandle)
		geometryShaderHandle?.let { glAttachShader(programHandle, it) }
		tessellationControlShaderHandle?.let { glAttachShader(programHandle, it) }
		tessellationEvaluationShaderHandle?.let { glAttachShader(programHandle, it) }

		glLinkProgram(programHandle)
		validateShaderLinking(programHandle)
		validateShaderProgram(programHandle)

		deleteShader(programHandle, vertexShaderHandle)
		deleteShader(programHandle, fragmentShaderHandle)
		geometryShaderHandle?.let { deleteShader(programHandle, it) }
		tessellationControlShaderHandle?.let { deleteShader(programHandle, it) }
		tessellationEvaluationShaderHandle?.let { deleteShader(programHandle, it) }

		val uniformParser = UniformParser()
		sourceBundle.sources.forEach(uniformParser::parse)
		return OpenGLShader(programHandle, uniformParser)
	}

	private fun compileShader(shaderSource: String, type: Int): Int {
		val shaderId = glCreateShader(type)

		glShaderSource(shaderId, shaderSource)
		glCompileShader(shaderId)

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			val info = getShaderInfoLog(shaderId)
			val shaderType = if (type == GL_VERTEX_SHADER) "Vertex" else "Fragment"
			throw Exception("$shaderType shader compilation failed: $info")
		}

		return shaderId
	}

	private fun deleteShader(programHandle: Int, shaderHandle: Int) {
		//glDetachShader(programHandle, shaderHandle)
		glDeleteShader(shaderHandle)
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

	private fun getProgramInfoLog(programId:Int):String {
		return glGetProgramInfoLog(programId, GL_INFO_LOG_LENGTH)
	}

	private fun getShaderInfoLog(shaderId:Int):String {
		return glGetShaderInfoLog(shaderId, GL_INFO_LOG_LENGTH)
	}
}

class ShaderSourceBundle(
	val vertexShader: String, val fragmentShader: String,
	val geometryShader: String? = null,
	val tessellationControlShader: String? = null,
	val tessellationEvaluationShader: String? = null
) {
	val sources = listOfNotNull(
		vertexShader, fragmentShader,
		geometryShader,
		tessellationControlShader, tessellationEvaluationShader
	)
}
