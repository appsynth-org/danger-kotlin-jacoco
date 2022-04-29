package net.appsynth.danger

import net.appsynth.danger.model.MetricType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class JaCoCoReportParserTest {

    private val jacocoReport = File(javaClass.classLoader.getResource("jacoco.xml").file)

    private val parser = JaCoCoReportParser()

    @Test
    fun `can parse all source file counters`() {
        val coverage = parser.parse(jacocoReport)

        assertEquals(0.5f, coverage?.coverageForFile(MetricType.INSTRUCTION, FILE1))
        assertEquals(0.25f, coverage?.coverageForFile(MetricType.LINE, FILE1))
        assertEquals(0.75f, coverage?.coverageForFile(MetricType.COMPLEXITY, FILE1))
        assertEquals(0.5f, coverage?.coverageForFile(MetricType.METHOD, FILE1))
        assertEquals(1.0f, coverage?.coverageForFile(MetricType.CLASS, FILE1))
    }

    @Test
    fun `no coverage from source file without counters`() {
        val coverage = parser.parse(jacocoReport)

        assertEquals(null, coverage?.coverageForFile(MetricType.INSTRUCTION, FILE2))
        assertEquals(null, coverage?.coverageForFile(MetricType.LINE, FILE2))
        assertEquals(null, coverage?.coverageForFile(MetricType.COMPLEXITY, FILE2))
        assertEquals(null, coverage?.coverageForFile(MetricType.METHOD, FILE2))
        assertEquals(null, coverage?.coverageForFile(MetricType.CLASS, FILE2))
    }

    @Test
    fun `no coverage for unknown source file`() {
        val coverage = parser.parse(jacocoReport)

        assertEquals(null, coverage?.coverageForFile(MetricType.INSTRUCTION, UNKNOWN_FILE))
        assertEquals(null, coverage?.coverageForFile(MetricType.LINE, UNKNOWN_FILE))
        assertEquals(null, coverage?.coverageForFile(MetricType.COMPLEXITY, UNKNOWN_FILE))
        assertEquals(null, coverage?.coverageForFile(MetricType.METHOD, UNKNOWN_FILE))
        assertEquals(null, coverage?.coverageForFile(MetricType.CLASS, UNKNOWN_FILE))
    }

    companion object {
        const val FILE1 = "featureflags/core/src/main/kotlin/net/appsynth/featureflags/FeatureFlag.kt"
        const val FILE2 = "featureflags/core/src/main/kotlin/net/appsynth/featureflags/Feature.kt"
        const val UNKNOWN_FILE = "some/other/projects/src/main/kotlin/net/appsynth/FooBar.kt"
    }
}
