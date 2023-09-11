package matt.cbor.diag

import matt.cbor.read.CborReader
import matt.cbor.read.streamman.readCbor
import matt.file.MFile
import matt.log.SystemOutLogger

fun MFile.diagnoseCbor() {
  val stream = inputStream()
  val oldDefaultLogger = CborReader.defaultLogger
  CborReader.defaultLogger = SystemOutLogger.apply {
	includeTimeInfo = false
  }
  val root = stream.readCbor()
  println(root)
  CborReader.defaultLogger = oldDefaultLogger
}