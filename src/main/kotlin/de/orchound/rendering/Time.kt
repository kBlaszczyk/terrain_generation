package de.orchound.rendering


object Time {
	var deltaTime = 0f
		private set

	private var currentMillis = System.currentTimeMillis()
	private var deltaMillis: Long = 0

	fun update() {
		val previousTime = currentMillis;
		currentMillis = System.currentTimeMillis()
		deltaMillis = currentMillis - previousTime
		deltaTime = deltaMillis / 1000f
	}
}
