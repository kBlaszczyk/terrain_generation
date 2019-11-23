package de.orchound.rendering

import de.orchound.rendering.display.Keys
import de.orchound.rendering.opengl.OpenGLMesh
import de.orchound.rendering.opengl.OpenGLTextureArray
import de.orchound.rendering.opengl.TerrainShader
import de.orchound.rendering.opengl.TextureLoader
import de.orchound.rendering.terrain.TerrainGenerator
import de.orchound.rendering.terrain.TerrainLayout
import de.orchound.rendering.terrain.TerrainSceneObject
import org.joml.Matrix4f
import org.joml.Vector3f


object TerrainApplication {
	private val terrainWidth = 512f
	private val camera = Camera(Window.aspectRatio, 90f, terrainWidth)
	private val shader: TerrainShader
	private val terrains: Collection<TerrainSceneObject>

	private val lightDirection = Vector3f(-10f)
	private val csLightDirection = Vector3f()
	private val layerColors = Array(10) { Vector3f() }
	private val layerLimits = FloatArray(10)
	private val layerBlendingHeights = FloatArray(10)
	private val terrainLayout = TerrainLayout()
	private val textureArray: OpenGLTextureArray

	init {
		Window.initialize()
		shader = TerrainShader()
		textureArray = TextureLoader.loadTextureArray(
			"/textures/water.png",
			"/textures/sandy_grass.png",
			"/textures/grass.png",
			"/textures/stoney_ground.png",
			"/textures/rocks_2.png",
			"/textures/rocks_1.png",
			"/textures/snow.png"
		)

		terrainLayout.addLayer("water", Color.fromHex("3662D0"), 9f, 8f)
		terrainLayout.addLayer("sand", Color.fromHex("E9E19E"), 11f, 10f)
		terrainLayout.addLayer("grass", Color.fromHex("50881B"), 20f, 18f)
		terrainLayout.addLayer("stoney ground", Color.fromHex("BD7118"), 22f, 20f)
		terrainLayout.addLayer("rocks low", Color.fromHex("6F6860"), 24f, 23f)
		terrainLayout.addLayer("rocks high", Color.fromHex("5F5D5B"), 33f, 31f)
		terrainLayout.addLayer("snow", Color.fromHex("F0EBE2"), 40f, 35f)

		terrainLayout.getColors(layerColors)
		terrainLayout.getLimits(layerLimits)
		terrainLayout.getBlendingHeights(layerBlendingHeights)

		val generator = TerrainGenerator(terrainWidth, 128, 25f)
		val terrainMesh = generator.generateTerrain()
		terrains = getTerrainsFromModel(terrainMesh)
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
		shader.setLayersCount(terrainLayout.layersCount)
		shader.setTextureArray(textureArray.handle)
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
				val offset = Vector3f(i * terrainWidth, 0f, j * terrainWidth)
				terrain.translation(offset)
				terrains.add(terrain)
			}
		}
		return terrains
	}
}
