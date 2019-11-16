package de.orchound.rendering


class Color(r: Int, g: Int, b: Int, a: Int = 255) {

	val r = r.toByte()
	val g = g.toByte()
	val b = b.toByte()
	val a = a.toByte()

	val rNormalized = r / 255f
	val gNormalized = g / 255f
	val bNormalized = b / 255f
	val aNormalized = a / 255f

	val rgba = (r shl 24) or (g shl 16) or (b shl 8) or a

	init {
		require(listOf(r, g, b, a).all { it in 0 .. 255 })
	}

	fun toRgbaBytes(): ByteArray {
		return ByteArray(3).apply {
			this[0] = r
			this[1] = g
			this[2] = b
			this[3] = a
		}
	}
	fun toRgbBytes(): ByteArray {
		return ByteArray(3).apply {
			this[0] = r
			this[1] = g
			this[2] = b
		}
	}

	companion object {
		fun fromNormalizedRgba(r: Float, g: Float, b: Float, a: Float = 1f): Color {
			return Color((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), (a * 255).toInt())
		}

		fun fromGrey(value: Int) = Color(value, value, value)
		fun fromNormalizedGrey(value: Float) = fromGrey((value * 255).toInt())

		fun fromHex(value: String): Color {
			val rgba = if (value.length == 6)
				(value + "FF").toLong(16).toInt()
			else value.toLong(16).toInt()

			return Color(
				rgba shr 24 and 0xFF, rgba shr 16 and 0xFF,
				rgba shr 8 and 0xFF, rgba and 0xFF
			)
		}
	}
}
