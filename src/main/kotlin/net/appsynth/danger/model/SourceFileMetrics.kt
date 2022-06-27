package net.appsynth.danger.model

import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Holds all metrics for a given source file.
 *
 * @param packageName name of the package the source file is in.
 * @param fileName source file name.
 * @constructor creates empty source file metrics.
 */
class SourceFileMetrics(
    private val packageName: String,
    private val fileName: String
) {
    private val metrics = mutableMapOf<MetricType, Counter>()

    /**
     * Adds coverage of specified metric [type].
     */
    fun setMetric(type: MetricType, missed: Int, covered: Int) {
        assert(missed >= 0) { "missed count can't have negative value" }
        assert(covered >= 0) { "covered count can't have negative value" }
        metrics[type] = Counter(missed, covered)
    }

    /**
     * Gets coverage of specified metric [type].
     *
     * @param type type of metric.
     * @return coverage ratio as a Float value [0.0, 1.0], or null if it's unknown.
     */
    fun getCoverage(type: MetricType): Float? = metrics[type]?.coverage()

    /**
     * Gets relative source file path.
     */
    fun path(): String = Path.of(packageName, fileName).pathString
}
