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
	private val layerBlendingHeights = FloatArray(10)

	init {
		Window.initialize()
		shader = TerrainShader()

		val terrainLayout = TerrainLayout().apply {
			this.addLayer("water deep", Color.fromHex("1824A9"), 5f, 4f)
			this.addLayer("water", Color.fromHex("3662D0"), 9f, 8f)
			this.addLayer("sand", Color.fromHex("E9E19E"), 11f, 10f)
			this.addLayer("grass low", Color.fromHex("73CB1D"), 13f, 12f)
			this.addLayer("grass high", Color.fromHex("50881B"), 15f, 14f)
			this.addLayer("rocks", Color.fromHex("BD7118"), 17f, 16f)
			this.addLayer("mountain", Color.fromHex("6F6860"), 19f, 18f)
			this.addLayer("mountain high", Color.fromHex("5F5D5B"), 24f, 23f)
			this.addLayer("snow", Color.fromHex("F0EBE2"), 25f, 24f)
		}

		terrainLayout.getColors(layerColors)
		terrainLayout.getLimits(layerLimits)
		terrainLayout.getBlendingHeights(layerBlendingHeights)

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
		shader.setLayerBlendingHeights(layerBlendingHeights)

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
