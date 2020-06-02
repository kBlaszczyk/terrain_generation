package de.orchound.rendering.shaderutility

interface UniformProvider {
	fun getUniformTypes(): Set<String>
	fun getUniforms(type: String): Set<String>
}