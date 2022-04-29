package net.appsynth.danger.model

import java.nio.file.Path
import kotlin.io.path.pathString

class SourceFileMetrics(
    private val packageName: String,
    private val fileName: String
) {
    private val metrics = mutableMapOf<MetricType, Counter>()

    fun setMetric(type: MetricType, missed: Int, covered: Int) {
        assert(missed >= 0) { "missed count can't have negative value" }
        assert(covered >= 0) { "covered count can't have negative value" }
        metrics[type] = Counter(missed, covered)
    }

    fun getCoverage(type: MetricType): Float? = metrics[type]?.coverage()

    fun path(): String = Path.of(packageName, fileName).pathString
}
