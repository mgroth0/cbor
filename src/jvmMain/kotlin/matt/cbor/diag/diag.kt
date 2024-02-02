package matt.cbor.diag

import matt.cbor.read.CborReader
import matt.cbor.read.streamman.readCbor
import matt.file.JioFile
import matt.log.SystemOutLogger

fun JioFile.diagnoseCbor() {
    val stream = inputStream()
    val oldDefaultLogger = CborReader.defaultLogger
    CborReader.defaultLogger = SystemOutLogger.apply {
        includeTimeInfo = false
    }
    val root = stream.readCbor()
    println(root)
    CborReader.defaultLogger = oldDefaultLogger
}
