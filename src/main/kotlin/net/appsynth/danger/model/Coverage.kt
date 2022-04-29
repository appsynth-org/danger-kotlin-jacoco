package net.appsynth.danger.model

import java.nio.file.Path

class Coverage {

    private val sourceFileMetricsMap = mutableMapOf<String, SourceFileMetrics>()
    private var maxPathDepth = 0

    fun addSourceFileMetrics(sourceFileMetrics: SourceFileMetrics) {
        sourceFileMetricsMap[sourceFileMetrics.path()] = sourceFileMetrics
        maxPathDepth = calculateMaxDepth()
    }

    fun aggregate(coverage: Coverage) {
        for (entry in coverage.sourceFileMetricsMap.entries) {
            sourceFileMetricsMap[entry.key] = entry.value
        }
        maxPathDepth = calculateMaxDepth()
    }

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
