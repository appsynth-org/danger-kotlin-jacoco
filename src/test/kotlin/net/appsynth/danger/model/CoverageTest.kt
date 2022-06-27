package net.appsynth.danger.model

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class CoverageTest {

    @Test
    fun `get coverage metric by file path`() {
        val coverage = COVERAGE.coverageForFile(
            MetricType.INSTRUCTION,
            "app/src/main/kotlin/com/example/model/Baz.kt"
        )

        assertThat(coverage).isEqualTo(0.0f)
    }

    @Test
    fun `coverage for unknown file is null`() {
        val coverage = COVERAGE.coverageForFile(
            MetricType.INSTRUCTION,
            "app/src/main/kotlin/com/example/model/None.kt"
        )

        assertThat(coverage).isNull()
    }

    @Test
    fun `coverage for missing metric type is null`() {
        val coverage = COVERAGE.coverageForFile(
            MetricType.BRANCH,
            "app/src/main/kotlin/com/example/Foo.kt"
        )

        assertThat(coverage).isNull()
    }

    @Test
    fun `aggregated coverage has has metrics for files from both sets`() {
        val coverage = Coverage()

        coverage.aggregate(COVERAGE)
        coverage.aggregate(COVERAGE2)

        assertThat(coverage.coverageForFile(MetricType.INSTRUCTION, "com/example/Foo.kt"))
            .isEqualTo(1.0f)
        assertThat(coverage.coverageForFile(MetricType.INSTRUCTION, "net/example/FooBar.kt"))
            .isEqualTo(0.75f)
    }

    @Test
    fun `coverage is empty for new instance`() {
        val coverage = Coverage()

        assertThat(coverage.isEmpty()).isTrue
    }

    @Test
    fun `coverage is not empty after aggregating`() {
        val coverage = Coverage().apply {
            aggregate(COVERAGE)
        }

        assertThat(coverage.isEmpty()).isFalse
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
