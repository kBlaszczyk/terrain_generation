package de.orchound.rendering

import de.orchound.rendering.display.Keys
import de.orchound.rendering.opengl.OpenGLMesh
import de.orchound.rendering.opengl.OpenGLTexture
import de.orchound.rendering.opengl.TerrainShader
import de.orchound.rendering.terrain.TerrainGenerator
import de.orchound.rendering.terrain.TerrainLayout
import de.orchound.rendering.terrain.TerrainSceneObject
import org.joml.Matrix4f
import org.joml.Vector3f


object TerrainApplication {
	private val terrainWidth = 256
	private val camera = Camera(Window.aspectRatio, 90f, (terrainWidth - 1).toFloat())
	private val shader: TerrainShader
	private val terrains: Collection<TerrainSceneObject>
	private val terrainTexture: OpenGLTexture

	private val lightDirection = Vector3f(-10f)
	private val csLightDirection = Vector3f()
	private val layerColors = Array(10) { Vector3f() }
	private val layerLimits = FloatArray(10)

	init {
		Window.initialize()
		shader = TerrainShader()

		val terrainLayout = TerrainLayout().apply {
			this.addLayer("water deep", 5f, Color.fromHex("1824A9"))
			this.addLayer("water", 9f, Color.fromHex("3662D0"))
			this.addLayer("sand", 10f, Color.fromHex("E9E19E"))
			this.addLayer("grass low", 12f, Color.fromHex("73CB1D"))
			this.addLayer("grass high", 14f, Color.fromHex("50881B"))
			this.addLayer("rocks", 16f, Color.fromHex("BD7118"))
			this.addLayer("mountain", 20f, Color.fromHex("6F6860"))
			this.addLayer("mountain high", 24f, Color.fromHex("2E3436"))
			this.addLayer("snow", 25f, Color.fromHex("F0EBE2"))
		}

		terrainLayout.getColors(layerColors)
		terrainLayout.getLimits(layerLimits)

		val generator = TerrainGenerator(terrainWidth, 16f)
		val meshTexturePair = generator.generateTerrain()
		terrains = getTerrainsFromModel(meshTexturePair.first)
		terrainTexture = meshTexturePair.second
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
		camera.getView(Matrix4f()).transformDirection(lightDirection, csLightDirection).normalize()
		terrains.forEach(TerrainSceneObject::update)
	}

	private fun render() {
		Window.prepareFrame()
		shader.bind()

		shader.setCsLightDirection(csLightDirection)
		shader.setTexture(terrainTexture.handle)
		shader.setLayerColors(layerColors)
		shader.setLayerLimits(layerLimits)

		terrains.forEach(TerrainSceneObject::draw)

		shader.unbind()
		Window.finishFrame()
	}

	private fun getTerrainsFromModel(mesh: OpenGLMesh): Collection<TerrainSceneObject> {
		val terrains = ArrayList<TerrainSceneObject>(9)
		for (i in -1 .. 1) {
			for (j in -1 .. 1) {
				val terrain = TerrainSceneObject(mesh, camera, shader)
				val offset = Vector3f(
					(i * (terrainWidth - 1)).toFloat(), 0f, (j * (terrainWidth - 1)).toFloat()
				)
				terrain.translation(offset)
				terrains.add(terrain)
			}
		}
		return terrains
	}
}
