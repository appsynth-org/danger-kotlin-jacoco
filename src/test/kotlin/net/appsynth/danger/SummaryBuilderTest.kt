package net.appsynth.danger

import net.appsynth.danger.model.Coverage
import net.appsynth.danger.model.MetricType
import net.appsynth.danger.model.SourceFileMetrics
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SummaryBuilderTest {

    @Test
    fun `summary has proper table headers`() {
        val builder = SummaryBuilder(COVERAGE, 10)

        val summary = builder.build(FILES)

        assertTrue(summary.contains("| Source file | Coverage |\n"))
        assertTrue(summary.contains("| --- | --- |\n"))
    }

    @Test
    fun `summary displays no coverage text for not covered files`() {
        val builder = SummaryBuilder(COVERAGE, 3)

        val summary = builder.build(listOf("net/appsynth/test/NotCovered.kt"))

        assertTrue(summary.contains("| NotCovered.kt | Not covered |"))
    }

    @Test
    fun `summary displays coverage percentages with 2 digit decimal point precision`() {
        val builder = SummaryBuilder(COVERAGE, 3)

        val summary = builder.build(FILES)

        assertTrue(summary.contains("| Foo.kt | 100.00% |"))
        assertTrue(summary.contains("| Bar.kt | 50.00% |"))
        assertTrue(summary.contains("| Baz.kt | 0.00% |"))
    }

    @Test
    fun `build summary shows all files when file count less than maximum`() {
        val builder = SummaryBuilder(COVERAGE, 3)

        val summary = builder.build(FILES)

        assertTrue(summary.contains("Foo.kt"))
        assertTrue(summary.contains("Bar.kt"))
        assertTrue(summary.contains("Baz.kt"))
    }

    @Test
    fun `add note about omitted files when file count more than maximum`() {
        val builder = SummaryBuilder(COVERAGE, 1)

        val summary = builder.build(FILES)

        assertTrue(summary.contains("2 more files omitted"))
    }

    companion object {
        val COVERAGE = Coverage().apply {
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "Foo.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 0, 10)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "Bar.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 5, 5)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "Baz.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 10, 0)
                }
            )
        }
        val FILES = listOf(
            "net/appsynth/test/Foo.kt",
            "net/appsynth/test/Bar.kt",
            "net/appsynth/test/Baz.kt"
        )
    }
}
