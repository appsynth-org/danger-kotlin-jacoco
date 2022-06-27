package net.appsynth.danger

import net.appsynth.danger.model.Coverage
import net.appsynth.danger.model.MetricType
import net.appsynth.danger.model.SourceFileMetrics
import java.io.File
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

/**
 * Implements fast parsing of JaCoCo XML reports.
 *
 * This parser will not perform any type of XML format or schema validation.
 */
internal class JaCoCoReportParser {

    private val inputFactory: XMLInputFactory = XMLInputFactory.newInstance().apply {
        setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false)
        setProperty(XMLInputFactory.IS_VALIDATING, false)
        setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
        setProperty(XMLInputFactory.SUPPORT_DTD, false)
    }

    /**
     * Parse single JaCoCo [reportFile].
     *
     * @return coverage holding object or null if the report didn't have "report" section.
     */
    fun parse(reportFile: File): Coverage? {
        val reader = inputFactory.createXMLEventReader(reportFile.inputStream())

        var packageName: String? = null
        var sourceFileMetrics: SourceFileMetrics? = null
        var coverage: Coverage? = null

        while (reader.hasNext()) {
            val nextEvent = reader.nextEvent()

            if (nextEvent.isStartElement) {
                val startElement = nextEvent.asStartElement()
                when (startElement.name.localPart) {
                    REPORT -> {
                        coverage = Coverage()
                    }
                    PACKAGE -> {
                        packageName = startElement.getAttributeByName(QName("name"))?.value
                    }
                    SOURCE_FILE -> {
                        val sourceFileName = startElement.getAttributeByName(QName("name"))?.value
                        sourceFileMetrics = SourceFileMetrics(packageName!!, sourceFileName!!)
                    }
                    COUNTER -> {
                        readMetric(startElement)?.let {
                            sourceFileMetrics?.setMetric(it.type, it.missed, it.covered)
                        }
                    }
                }
            }

            if (nextEvent.isEndElement) {
                when (nextEvent.asEndElement().name.localPart) {
                    PACKAGE -> packageName = null
                    SOURCE_FILE -> {
                        coverage?.addSourceFileMetrics(sourceFileMetrics!!)
                        sourceFileMetrics = null
                    }
                }
            }
        }
        return coverage
    }

    private data class SingleMetric(val type: MetricType, val missed: Int, val covered: Int)

    private fun readMetric(element: StartElement): SingleMetric? {
        val type = element.getAttributeByName(QName("type"))?.value
        val missed = element.getAttributeByName(QName("missed"))?.value
        val covered = element.getAttributeByName(QName("covered"))?.value
        var metric: SingleMetric? = null

        MetricType.values().find {it.name == type }?.let {
            metric = if (missed != null && covered != null) {
                try {
                    SingleMetric(it, missed.toInt(), covered.toInt())
                } catch (e: java.lang.NumberFormatException) {
                    null
                }
            } else {
                null
            }
        }

        return metric
    }

    companion object {
        const val REPORT = "report"
        const val SESSION_INFO = "sessioninfo"
        const val PACKAGE = "package"
        const val CLASS = "class"
        const val METHOD = "method"
        const val LINE = "line"
        const val SOURCE_FILE = "sourcefile"
        const val COUNTER = "counter"
    }
}
