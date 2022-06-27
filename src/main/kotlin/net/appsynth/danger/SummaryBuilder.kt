package net.appsynth.danger

import net.appsynth.danger.model.Coverage
import net.appsynth.danger.model.MetricType
import kotlin.io.path.Path

/**
 * Builds formatted coverage reports.
 */
class SummaryBuilder(
    private val coverage: Coverage,
    private val maxReportedFiles: Int,
    private val referenceCoverage: Coverage = Coverage()
) {
    /**
     * Prepares coverage report summary.
     *
     * @param reportedFiles source file paths to include in the summary.
     * @return summary formatted in Markdown format
     */
    fun build(reportedFiles: List<String>): String {
        val sourceFileCoverage = mutableListOf<SourceFileCoverage>()

        for (filePath in reportedFiles) {
            sourceFileCoverage.add(
                SourceFileCoverage(
                    Path(filePath).fileName.toString(),
                    coverage.coverageForFile(MetricType.INSTRUCTION, filePath),
                    referenceCoverage.coverageForFile(MetricType.INSTRUCTION, filePath)
                )
            )
        }
        sourceFileCoverage.sortByDescending { it.coverage }

        return buildString {
            append("### Coverage Summary\n\n")
            if (referenceCoverage.isEmpty()) {
                append("| Source file | Coverage |\n")
                append("| --- | --- |\n")
            } else {
                append("| Source file | Coverage | Trend |\n")
                append("| --- | --- | --- |\n")
            }

            for (fileCoverage in sourceFileCoverage.take(maxReportedFiles)) {
                val line = if (referenceCoverage.isEmpty()) {
                    coverageLine(fileCoverage.fileName, fileCoverage.coverage)
                } else {
                    coverageDiffLine(fileCoverage.fileName, fileCoverage.coverage, fileCoverage.referenceCoverage)
                }
                append(line)
            }

            if (reportedFiles.size > maxReportedFiles) {
                append("${reportedFiles.size - maxReportedFiles} more files omitted in this summary.")
            }

            append("\n\n")
        }
    }

    private fun coverageLine(fileName: String, coverageRatio: Float?): String {
        val displayedCoverage = coverageRatio?.toCoveragePercent() ?: "Not covered"
        return "| $fileName | $displayedCoverage |\n"
    }

    private fun coverageDiffLine(fileName: String, coverageRatio: Float?, referenceCoverageRatio: Float?): String {
        val currentCoverage = coverageRatio ?: 0.0f
        val referenceCoverage = referenceCoverageRatio ?: 0.0f
        val displayedCoverage = if (currentCoverage == 0.0f && referenceCoverage == 0.0f) {
            "Not covered"
        } else {
            "${currentCoverage.toCoveragePercent()} (${(currentCoverage - referenceCoverage).toDiff()})"
        }
        val trend = when {
            currentCoverage - referenceCoverage > 0 -> ":chart_with_upwards_trend:"
            currentCoverage - referenceCoverage < 0 -> ":chart_with_downwards_trend:"
            else -> ":heavy_minus_sign:"
        }
        return "| $fileName | $displayedCoverage | $trend |\n"
    }

    private fun Float.toCoveragePercent(): String = "%.2f%%".format(this * 100)

    private fun Float.toDiff(): String = "%+.2f".format(this * 100)

    private data class SourceFileCoverage(
        val fileName: String,
        val coverage: Float?,
        val referenceCoverage: Float?
    )
}
