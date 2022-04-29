package net.appsynth.danger.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoverageTest {

    @Test
    fun `get coverage metric by file path`() {
        val coverage = COVERAGE.coverageForFile(
            MetricType.INSTRUCTION,
            "app/src/main/kotlin/com/example/model/Baz.kt"
        )

        assertEquals(0.0f, coverage)
    }

    @Test
    fun `coverage for unknown file is null`() {
        val coverage = COVERAGE.coverageForFile(
            MetricType.INSTRUCTION,
            "app/src/main/kotlin/com/example/model/None.kt"
        )

        assertEquals(null, coverage)
    }

    @Test
    fun `coverage for missing metric type is null`() {
        val coverage = COVERAGE.coverageForFile(
            MetricType.BRANCH,
            "app/src/main/kotlin/com/example/Foo.kt"
        )

        assertEquals(null, coverage)
    }

    @Test
    fun `aggregated coverage has has metrics for files from both sets`() {
        val coverage = Coverage()

        coverage.aggregate(COVERAGE)
        coverage.aggregate(COVERAGE2)

        assertEquals(1.0f, coverage.coverageForFile(MetricType.INSTRUCTION, "com/example/Foo.kt"))
        assertEquals(0.75f, coverage.coverageForFile(MetricType.INSTRUCTION, "net/example/FooBar.kt"))
    }

    companion object {
        val COVERAGE = Coverage().apply {
            addSourceFileMetrics(
                SourceFileMetrics("com/example", "Foo.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 0, 99)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("com/example", "Bar.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 50, 50)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("com/example/model", "Baz.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 100, 0)
                }
            )
        }
        val COVERAGE2 = Coverage().apply {
            addSourceFileMetrics(
                SourceFileMetrics("net/example", "FooBar.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 25, 75)
                }
            )
        }
    }
}
