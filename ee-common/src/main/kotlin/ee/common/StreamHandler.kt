package ee.common

import org.slf4j.Logger
import java.io.InputStream

open class StreamHandler : Thread {
    val inputStream: InputStream
    val filterPattern: Regex
    val filter: Boolean
    val outputProcessor: (String) -> Unit
    val log: Logger?

    constructor(inputStream: InputStream, filter: Boolean = false, filterPattern: Regex = "".toRegex(),
        log: Logger? = null, outputProcessor: (String) -> Unit = {}) {
        this.inputStream = inputStream
        this.filterPattern = filterPattern
        this.filter = filter
        this.outputProcessor = outputProcessor
        this.log = log
    }

    override fun run() {
        if (this.filter) {
            inputStream.reader().forEachLine {
                if (filterPattern.matches(it.toLowerCase())) {
                    it.process()
                }
            }
        } else {
            inputStream.reader().forEachLine { it.process() }
        }
    }

    private fun String.process() {
        log?.info(this)
        outputProcessor(this)
    }
}
