package de.orchound.rendering

import org.joml.Vector3f
import java.nio.ByteBuffer


fun FloatArray.toByteBuffer(endianInversion: Boolean = false): ByteBuffer {
	val vertexByteBuffer = ByteBuffer.allocateDirect(this.size * 4)
	for (value in this) {
		val floatBytes = ByteArray(4)
		ByteBuffer.wrap(floatBytes).putFloat(value)
		if (endianInversion)
			floatBytes.invertEndianness()
		vertexByteBuffer.put(floatBytes)
	}
	vertexByteBuffer.flip()
	return vertexByteBuffer
}

fun IntArray.toByteBuffer(endianInversion: Boolean = false): ByteBuffer {
	val vertexByteBuffer = ByteBuffer.allocateDirect(this.size * 4)
	for (value in this) {
		val intBytes = ByteArray(4)
		ByteBuffer.wrap(intBytes).putInt(value)
		if (endianInversion)
			intBytes.invertEndianness()
		vertexByteBuffer.put(intBytes)
	}
	vertexByteBuffer.flip()
	return vertexByteBuffer
}

fun ByteArray.invertEndianness() {
	for (index in 0 until this.size / 2) {
		val otherIndex = this.size - 1 - index
		val tmp = this[index]
		this[index] = this[otherIndex]
		this[otherIndex] = tmp
	}
}

/**
 * Applies vector3-normalization to the values of the provided float array.
 * The values are considered to represent consecutively stored 3 component vectors.
 */
fun FloatArray.vec3Normalization() {
	for (normalIndex in this.indices step 3) {
		val normal = Vector3f(
			this[normalIndex], this[normalIndex + 1], this[normalIndex + 2]
		).normalize()
		this[normalIndex] = normal.x
		this[normalIndex + 1] = normal.y
		this[normalIndex + 2] = normal.z
	}
}
