package de.orchound.rendering.shaderutility

import java.util.regex.Pattern

class UniformParser : UniformProvider {

	private val uniformsByType = HashMap<String, MutableSet<String>>()

	fun parse(source: String) {
		parseUniforms(source)
		parseArrayUniforms(source)
	}

	private fun parseUniforms(source: String) {
		val pattern = Pattern.compile("""^uniform (\w+) (\w+);$""", Pattern.MULTILINE)
		val matcher = pattern.matcher(source)
		while (matcher.find()) {
			val type = matcher.group(1)
			val uniform = matcher.group(2)
			uniformsByType.getOrPut(type) { HashSet() }.add(uniform)
		}
	}

	private fun parseArrayUniforms(source: String) {
		val pattern = Pattern.compile("""^uniform (\w+) (\w+)\[\w+];$""", Pattern.MULTILINE)
		val matcher = pattern.matcher(source)
		while (matcher.find()) {
			val type = matcher.group(1)
			val uniform = matcher.group(2)
			uniformsByType.getOrPut("$type[]") { HashSet() }.add(uniform)
		}
	}

	override fun getUniformTypes(): Set<String> = uniformsByType.keys

	override fun getUniforms(type: String): Set<String> {
		return uniformsByType.getOrDefault(type, emptySet())
	}
}
