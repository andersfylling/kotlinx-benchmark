package org.jetbrains.gradle.benchmarks

import kotlinx.cinterop.*
import platform.posix.*

actual fun Double.format(precision: Int): String {
    val longPart = toLong()
    val fractional = this - longPart
    val thousands = longPart.toString().replace(Regex("\\B(?=(\\d{3})+(?!\\d))"), ",")
    if (fractional < DBL_EPSILON || precision == 0)
        return thousands

    return memScoped {
        val bytes = allocArray<ByteVar>(100)
        sprintf(bytes, "%.${precision}F", fractional)
        val fractionText = bytes.toKString()
        thousands + fractionText.removePrefix("0")
    }
}

actual fun saveReport(reportFile: String?, results: Collection<ReportBenchmarkResult>) {
    if (reportFile == null)
        return

    val file = fopen(reportFile, "w")
    fputs(formatJson(results), file)
    fclose(file)
}