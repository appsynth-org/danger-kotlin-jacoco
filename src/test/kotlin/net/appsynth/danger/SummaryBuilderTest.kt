package net.appsynth.danger

import net.appsynth.danger.model.Coverage
import net.appsynth.danger.model.MetricType
import net.appsynth.danger.model.SourceFileMetrics
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class SummaryBuilderTest {

    @Test
    fun `summary has proper table headers`() {
        val builder = SummaryBuilder(COVERAGE, 10)

        val summary = builder.build(FILES)

        assertThat(summary)
            .contains("| **Source file** | **Coverage** |\n")
            .contains("| --- | --- |\n")
    }

    @Test
    fun `summary displays no coverage text for not covered files`() {
        val builder = SummaryBuilder(COVERAGE, 3)

        val summary = builder.build(listOf("net/appsynth/test/NotCovered.kt"))

        assertThat(summary).contains("| NotCovered.kt | Not covered |")
    }

    @Test
    fun `summary displays coverage percentages with 2 digit decimal point precision`() {
        val builder = SummaryBuilder(COVERAGE, 4)

        val summary = builder.build(FILES)

        assertThat(summary)
            .contains("| Foo.kt | 100.00% |")
            .contains("| Bar.kt | 50.00% |")
            .contains("| Baz.kt | 0.00% |")
            .contains("| FooBar.kt | 1.00% |")
    }

    @Test
    fun `build summary shows all files when file count less than maximum`() {
        val builder = SummaryBuilder(COVERAGE, 5)

        val summary = builder.build(FILES)

        assertThat(summary)
            .contains("Foo.kt")
            .contains("Bar.kt")
            .contains("Baz.kt")
            .contains("FooBar.kt")
            .contains("BarBaz.kt")
    }

    @Test
    fun `add note about omitted files when file count more than maximum`() {
        val builder = SummaryBuilder(COVERAGE, 1)

        val summary = builder.build(FILES)

        assertThat(summary).contains("4 more files omitted")
    }

    @Test
    fun `show header for diff summary`() {
        val builder = SummaryBuilder(COVERAGE, 0, REF_COVERAGE)

        val summary = builder.build(FILES)

        assertThat(summary)
            .contains("| **Source file** | **Coverage** |\n")
            .contains("| --- | --- |\n")
    }

    @Test
    fun `show coverage value difference and trend for diff summary`() {
        val builder = SummaryBuilder(COVERAGE, 5, REF_COVERAGE)

        val summary = builder.build(FILES)

        assertThat(summary)
            .contains("| Foo.kt | 100.00% (+0.00) :heavy_minus_sign: |")
            .contains("| Bar.kt | 50.00% (+25.00) :small_red_triangle: |")
            .contains("| Baz.kt | 0.00% (-25.00) :small_red_triangle_down: |")
            .contains("| FooBar.kt | 1.00% (+1.00) :small_red_triangle: |")
            .contains("| BarBaz.kt | 0.00% (-1.00) :small_red_triangle_down: |")
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
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "FooBar.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 99, 1)
                }
            )
        }
        val REF_COVERAGE = Coverage().apply {
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "Foo.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 0, 10)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "Bar.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 75, 25)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "Baz.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 75, 25)
                }
            )
            addSourceFileMetrics(
                SourceFileMetrics("net/appsynth/test", "BarBaz.kt").apply {
                    setMetric(MetricType.INSTRUCTION, 99, 1)
                }
            )
        }
        val FILES = listOf(
            "net/appsynth/test/Foo.kt",
            "net/appsynth/test/Bar.kt",
            "net/appsynth/test/Baz.kt",
            "net/appsynth/test/FooBar.kt",
            "net/appsynth/test/BarBaz.kt"
        )
    }
}
