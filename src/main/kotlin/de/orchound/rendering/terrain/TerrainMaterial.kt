package de.orchound.rendering.terrain

import de.orchound.rendering.shaderutility.OpenGLShader
import de.orchound.rendering.shaderutility.UniformJomlHelper
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer


class TerrainMaterial(private val shader: OpenGLShader, layersCount: Int) {

	private val jomlHelper = UniformJomlHelper(shader)

	private val modelSetter = jomlHelper.getMat4Setter("model")
	private val modelViewSetter = jomlHelper.getMat4Setter("model_view")
	private val modelViewProjectionSetter = jomlHelper.getMat4Setter("model_view_projection")
	private val lightDirectionSetter = jomlHelper.getVec3Setter("light_direction_cs")
	private val tessellationOriginSetter = jomlHelper.getVec2Setter("tessellation_origin")
	private val tessellationWidthSetter = shader.getFloatSetter("tessellation_width")
	private val layersCountSetter = shader.getIntSetter("layers_count")
	private val layerLimitsSetter = shader.getFloatArraySetter("layer_limits")
	private val layerBlendingHeightsSetter = shader.getFloatArraySetter("layer_blending_heights")

	private val floatBuffer = BufferUtils.createFloatBuffer(layersCount)

	init {
		shader.bind()
		shader.getSamplerSetter("layer_textures")(0)
		shader.getSamplerSetter("height_map")(1)
		shader.getSamplerSetter("normal_map")(2)
		shader.unbind()
	}

	fun setModel(matrix: Matrix4f) = modelSetter(matrix)
	fun setModelView(matrix: Matrix4f) = modelViewSetter(matrix)
	fun setModelViewProjection(matrix: Matrix4f) = modelViewProjectionSetter(matrix)
	fun setCsLightDirection(vector: Vector3f) = lightDirectionSetter(vector)
	fun setTessellationOrigin(vector: Vector2f) = tessellationOriginSetter(vector)
	fun setTessellationWidth(width: Float) = tessellationWidthSetter(width)
	fun setLayersCount(value: Int) = layersCountSetter(value)

	fun setTextureArray(handle: Int) = shader.setTexture(handle)
	fun setHeightMap(handle: Int) = shader.setTexture(handle, 1)
	fun setNormalMap(handle: Int) = shader.setTexture(handle, 2)

	fun setLayerLimits(values: FloatArray) = setFloats(layerLimitsSetter, values)
	fun setLayerBlendingHeights(values: FloatArray) = setFloats(layerBlendingHeightsSetter, values)

	private fun setFloats(setter: (FloatBuffer) -> Unit, values: FloatArray) {
		floatBuffer.clear()
		floatBuffer.put(values).flip()
		setter(floatBuffer)
	}
}
