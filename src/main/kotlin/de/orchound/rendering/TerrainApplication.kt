package de.orchound.rendering

import de.orchound.rendering.opengl.OpenGLTexture
import de.orchound.rendering.opengl.Quad
import de.orchound.rendering.opengl.QuadShader
import de.orchound.rendering.terrain.NoiseGenerator
import de.orchound.rendering.terrain.NoiseTexture


object TerrainApplication {
	private val window = Window("Terrain Generation", 800, 800)
	private val quad = Quad()
	private val shader = QuadShader()
	private val noiseTexture: OpenGLTexture

	init {
		val noise = NoiseGenerator.generateNoiseMap(800, 800)
		noiseTexture = NoiseTexture(noise)
	}

	fun run() {
		while (!window.shouldClose()) {
			update()
			render()
		}

		window.destroy()
	}

	private fun update() {}

	private fun render() {
		window.prepareFrame()

		shader.bind()
		shader.setTexture(noiseTexture.handle)
		quad.draw()
		shader.unbind()

		window.finishFrame()
	}
}
