package de.orchound.rendering

import de.orchound.rendering.display.Keys
import de.orchound.rendering.opengl.TerrainShader
import de.orchound.rendering.terrain.TerrainGenerator
import de.orchound.rendering.terrain.TerrainLayout
import de.orchound.rendering.terrain.TerrainSceneObject
import org.joml.Vector3f


object TerrainApplication {
	private val terrainWidth = 256
	private val camera = Camera(Window.aspectRatio, 90f, terrainWidth.toFloat())
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
			this.addLayer("grass high", 1.3f, Color.fromHex("50881B"))
			this.addLayer("rocks", 1.5f, Color.fromHex("BD7118"))
			this.addLayer("mountain", 1.8f, Color.fromHex("6F6860"))
			this.addLayer("mountain high", 1.9f, Color.fromHex("2E3436"))
			this.addLayer("snow", 2f, Color.fromHex("F0EBE2"))
		}
		val generator = TerrainGenerator(terrainWidth, terrainLayout, 8f)
		terrain = generator.generateTerrain()
		terrain.camera = camera
		terrain.shader = shader
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
		terrain.update()
	}

	private fun render() {
		Window.prepareFrame()
		shader.bind()

		for (i in -1 .. 1) {
			for (j in -1 .. 1) {
				val offset = Vector3f(
					(i * (terrainWidth - 1)).toFloat(), 0f, (j * (terrainWidth - 1)).toFloat()
				)
				terrain.translate(offset)
				terrain.draw()
			}
		}

		shader.unbind()
		Window.finishFrame()
	}
}
