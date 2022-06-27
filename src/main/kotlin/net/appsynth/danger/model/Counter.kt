package net.appsynth.danger.model

/**
 * Hit/miss coverage counter.
 */
internal data class Counter(
    val missed: Int,
    val covered: Int
) {
    /**
     * Coverage ratio.
     *
     * @return coverage ratio as float value from [0.0, 1.0] range, or null if both covered and missed counters are 0.
     */
    fun coverage(): Float? =
        if (covered + missed == 0) {
            null
        } else {
            covered.toFloat() / (covered + missed)
        }
}
