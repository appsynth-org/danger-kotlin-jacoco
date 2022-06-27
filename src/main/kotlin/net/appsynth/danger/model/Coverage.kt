package net.appsynth.danger.model

import java.nio.file.Path

/**
 * Holds aggregated coverage information.
 */
class Coverage {

    private val sourceFileMetricsMap = mutableMapOf<String, SourceFileMetrics>()
    private var maxPathDepth = 0

    /**
     * Adds source file metrics.
     */
    fun addSourceFileMetrics(sourceFileMetrics: SourceFileMetrics) {
        sourceFileMetricsMap[sourceFileMetrics.path()] = sourceFileMetrics
        maxPathDepth = calculateMaxDepth()
    }

    /**
     * Aggregate coverage from another report.
     */
    fun aggregate(coverage: Coverage) {
        for (entry in coverage.sourceFileMetricsMap.entries) {
            sourceFileMetricsMap[entry.key] = entry.value
        }
        maxPathDepth = calculateMaxDepth()
    }

    /**
     * Looks up coverage ratio of a specified [metricType] for a source file.
     *
     * @param metricType type of metric.
     * @param filePath relative file path containing only package and file name i.e.: "com/example/foo/Bar.kt".
     * @return coverage ratio as a float value in range [0.0, 1.0], or null if no matching data found.
     */
    fun coverageForFile(metricType: MetricType, filePath: String): Float? =
        findMetricsByFilePath(filePath)?.getCoverage(metricType)

    private fun calculateMaxDepth(): Int = sourceFileMetricsMap.keys.maxOfOrNull { Path.of(it).toList().size } ?: 0

    private fun findMetricsByFilePath(filePath: String): SourceFileMetrics? {
        val path = Path.of(filePath)
        val pathElements = path.toList().size

        val startIndex = if (pathElements < maxPathDepth) 0 else pathElements - maxPathDepth
        for (i in startIndex until pathElements) {
            val subPath = path.subpath(i, pathElements).toString()
            if (sourceFileMetricsMap.containsKey(subPath)) return sourceFileMetricsMap[subPath]
        }
        return null
    }
}
