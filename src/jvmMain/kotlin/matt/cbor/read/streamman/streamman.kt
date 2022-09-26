package matt.cbor.read.streamman

import matt.cbor.CborItemReader
import matt.cbor.data.major.CborDataItem
import matt.cbor.data.major.seven.Break
import matt.stream.readOrNullIfEOF
import java.io.BufferedInputStream
import java.io.InputStream

fun InputStream.readCbor(): CborDataItem {
  val reader = cborReader()
  val item = reader.read()
  close()
  return item
}


fun InputStream.cborReader(): CborItemReader {
  val man = CborStreamManager(this)
  val itemReader = CborItemReader()
  itemReader.initStreamMan(man)
  return itemReader
}

class CborStreamManager internal constructor(stream: InputStream) {
  internal var isInitialized = false
  private val stream = (stream as? BufferedInputStream) ?: stream.buffered()
  fun read() = stream.read()
  fun readOrNullIfEOF() = stream.readOrNullIfEOF()
  fun readNBytes(len: Int) = stream.readNBytes(len)
  protected fun readUntilBreak(): List<Byte> {
	val bytes = mutableListOf<Byte>()
	do {
	  val b = read().toByte()
	  if (b != Break.byte) {
		bytes += b
	  }
	} while (b != Break.byte)
	return bytes
  }
}