package net.appsynth.danger

import net.appsynth.danger.model.Coverage
import net.appsynth.danger.model.MetricType
import kotlin.io.path.Path

/**
 * Builds formatted coverage reports.
 */
class SummaryBuilder(
    private val coverage: Coverage,
    private val maxReportedFiles: Int
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
                    coverage.coverageForFile(MetricType.INSTRUCTION, filePath)
                )
            )
        }
        sourceFileCoverage.sortByDescending { it.coverage }

        return buildString {
            append("### Coverage Summary\n\n")
            append("| Source file | Coverage |\n")
            append("| --- | --- |\n")

            for (fileCoverage in sourceFileCoverage.take(maxReportedFiles)) {
                val displayedCoverage = if (fileCoverage.coverage != null) {
                    fileCoverage.coverage.toCoveragePercent()
                } else {
                    "Not covered"
                }

                append("| ${fileCoverage.fileName} | $displayedCoverage |\n")
            }

            if (reportedFiles.size > maxReportedFiles) {
                append("${reportedFiles.size - maxReportedFiles} more files omitted in this summary.")
            }

            append("\n\n")
        }
    }

    private fun Float.toCoveragePercent(): String = "%.2f%%".format(this * 100)

    private data class SourceFileCoverage(
        val fileName: String,
        val coverage: Float?
    )
}
