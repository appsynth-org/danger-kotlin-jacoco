package net.appsynth.danger.model

internal data class Counter(
    val missed: Int,
    val covered: Int
) {
    fun coverage(): Float? =
        if (covered + missed == 0) {
            null
        } else {
            covered.toFloat() / (covered + missed)
        }
}
