package de.orchound.rendering

import de.orchound.rendering.display.Keys
import de.orchound.rendering.opengl.TerrainShader
import de.orchound.rendering.terrain.TerrainGenerator
import de.orchound.rendering.terrain.TerrainLayout
import de.orchound.rendering.terrain.TerrainSceneObject


object TerrainApplication {
	private val camera = Camera(Window.aspectRatio, 90f)
	private val shader: TerrainShader
	private val terrain: TerrainSceneObject

	init {
		Window.initialize()
		shader = TerrainShader()

		val terrainLayout = TerrainLayout().apply {
			this.addLayer("water deep", 0.3f, Color.fromHex("1824A9"))
			this.addLayer("water", 0.5f, Color.fromHex("3662D0"))
			this.addLayer("sand", 0.6f, Color.fromHex("E9E19E"))
			this.addLayer("grass low", 0.8f, Color.fromHex("73CB1D"))
			this.addLayer("grass high", 1f, Color.fromHex("50881B"))
			this.addLayer("rocks", 1.2f, Color.fromHex("BD7118"))
			this.addLayer("mountain", 1.7f, Color.fromHex("6F6860"))
			this.addLayer("mountain high", 1.9f, Color.fromHex("2E3436"))
			this.addLayer("snow", 2f, Color.fromHex("F0EBE2"))
		}
		val generator = TerrainGenerator(256, terrainLayout, 20f)
		terrain = generator.generateTerrain()
	}

	fun run() {
		while (!Window.getPressedKeys().contains(Keys.Q) && !Window.shouldClose()) {
			update()
			render()
		}
		Window.destroy()
	}

	private fun update() {
		camera.update()
		terrain.preparePerspective(camera)
		terrain.update()
	}

	private fun render() {
		Window.prepareFrame()

		shader.bind()
		terrain.prepareShader(shader)
		terrain.draw()
		shader.unbind()

		Window.finishFrame()
	}
}
