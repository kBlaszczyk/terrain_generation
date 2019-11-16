package de.orchound.rendering

import de.orchound.rendering.opengl.Quad
import de.orchound.rendering.opengl.QuadShader
import de.orchound.rendering.terrain.TerrainGenerator
import de.orchound.rendering.terrain.TerrainLayout
import de.orchound.rendering.terrain.TerrainSceneObject


object TerrainApplication {
	private val window = Window("Terrain Generation", 800, 800)
	private val quad = Quad()
	private val shader = QuadShader()
	private val terrain: TerrainSceneObject

	init {
		val terrainLayout = TerrainLayout().apply {
			this.addLayer("water deep", 0.3f, Color.fromHex("1824A9"))
			this.addLayer("water", 0.5f, Color.fromHex("3662D0"))
			this.addLayer("sand", 0.6f, Color.fromHex("E9E19E"))
			this.addLayer("grass low", 0.9f, Color.fromHex("73CB1D"))
			this.addLayer("grass high", 1.3f, Color.fromHex("50881B"))
			this.addLayer("rocks", 1.5f, Color.fromHex("BD7118"))
			this.addLayer("mountain", 1.7f, Color.fromHex("6F6860"))
			this.addLayer("mountain high", 1.9f, Color.fromHex("2E3436"))
			this.addLayer("snow", 2f, Color.fromHex("F0EBE2"))
		}
		val generator = TerrainGenerator(terrainLayout)
		terrain = generator.generateTerrain()
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
		shader.setTexture(terrain.texture.handle)
		quad.draw()
		shader.unbind()

		window.finishFrame()
	}
}
