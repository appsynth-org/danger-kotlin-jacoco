package net.appsynth.danger

import net.appsynth.danger.model.Coverage
import net.appsynth.danger.model.MetricType
import systems.danger.kotlin.sdk.DangerPlugin
import java.io.File

fun jacoco(block: JaCoCoPlugin.() -> Unit) = JaCoCoPlugin.run(block)

object JaCoCoPlugin : DangerPlugin() {
    private const val MAX_REPORTED_FILES = 20

    override val id: String = this.javaClass.name

    private val coverage = Coverage()
    private val parser = JaCoCoReportParser()

    var maxReportedFiles = MAX_REPORTED_FILES

    var noCoverageWarning = true

    var excludePatterns = emptyList<Regex>()

    fun parse(vararg reportFiles: File) {
        for (file in reportFiles) {
            parser.parse(file)?.let {
                coverage.aggregate(it)
            }
        }
    }

    fun report(filePaths: List<String>) {
        val filesToReport = filePaths.filterNot { filePath ->
            excludePatterns.any { it.matches(filePath) }
        }

        if (filesToReport.isNotEmpty()) {
            val noCoverageFiles = filesToReport.count { coverage.coverageForFile(MetricType.INSTRUCTION, it) == null }
            if (noCoverageFiles > 0 && noCoverageWarning) {
                context.warn("$noCoverageFiles source files have no coverage. Consider adding some more tests.")
            }

            val builder = SummaryBuilder(
                coverage,
                maxReportedFiles
            )
            context.markdown(builder.build(filesToReport))
        }
    }
}
