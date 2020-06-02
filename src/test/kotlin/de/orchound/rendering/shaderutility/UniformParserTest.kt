package de.orchound.rendering.shaderutility

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.matchers.collections.containExactlyInAnyOrder
import io.kotlintest.should
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class UniformParserTest {

	private val sources = listOf(
		"""	|#version 330 core
			|
			|uniform vec3 color;
			|uniform vec3 light_direction;
			|uniform mat4 model_view_projection;
			|
			|void main() {}
			|""".trimMargin(),
		"""	|#version 330 core
			|
			|uniform vec2 uv;
			|uniform float layer_limits[MAX_LAYERS];
			|uniform sampler2D height_map;
			|uniform sampler2DArray layer_textures;
			|
			|void main() {}
			|""".trimMargin()
	)

	private lateinit var uniformParser: UniformParser

	@BeforeEach
	fun setUp() {
		uniformParser = UniformParser()
		sources.forEach(uniformParser::parse)
	}

	@Test
	fun testUniformTypeCollecting() {
		uniformParser.getUniformTypes() should containExactlyInAnyOrder(
			"vec2", "vec3", "mat4", "float[]", "sampler2D", "sampler2DArray"
		)
	}

	@Test
	fun testUniformValueParsing() {
		val vec3Uniforms = uniformParser.getUniforms("vec3")
		vec3Uniforms should containExactlyInAnyOrder("color", "light_direction")

		val mat4Uniforms = uniformParser.getUniforms("mat4")
		mat4Uniforms should containExactly("model_view_projection")

		val vec2Uniforms = uniformParser.getUniforms("vec2")
		vec2Uniforms should containExactly("uv")

		val floatArrayUniforms = uniformParser.getUniforms("float[]")
		floatArrayUniforms should containExactly("layer_limits")

		val sampler2DUniforms = uniformParser.getUniforms("sampler2D")
		sampler2DUniforms should containExactly("height_map")

		val sampler2DArrayUniforms = uniformParser.getUniforms("sampler2DArray")
		sampler2DArrayUniforms should containExactly("layer_textures")
	}

	@Test
	fun nonPresentTypeReturnsEmptySet() {
		uniformParser.getUniforms("xxx") should beEmpty()
	}
}
