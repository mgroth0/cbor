package matt.cbor.read.streamman

import matt.cbor.data.major.CborDataItem
import matt.cbor.data.major.seven.CborBreak
import matt.cbor.read.item.CborItemReader
import matt.lang.NOT_IMPLEMENTED
import matt.stream.readOrNullIfEOF
import java.io.BufferedInputStream
import java.io.InputStream

fun InputStream.readCbor(): CborDataItem<*> {
  val reader = cborReader()
  val item = reader.read()
  close()
  return item
}

fun ByteArray.cborReader() = inputStream().cborReader()

fun InputStream.cborReader(): CborItemReader {
  val man = CborStreamManager(this)
  val itemReader = CborItemReader()
  itemReader.initStreamMan(man)
  return itemReader
}

open class CborStreamManager internal constructor(stream: InputStream) {

  internal var isInitialized = false
  private val stream = (stream as? BufferedInputStream) ?: stream.buffered()

  open fun read() = stream.read()
  open fun readOrNullIfEOF() = stream.readOrNullIfEOF()


  open fun readNBytes(len: Int) = stream.readNBytes(len) /*WARN: UNDEFINED BEHAVIOR IF EOF IS REACHED*/

  protected open fun readUntilBreak(): List<Byte> {
	val bytes = mutableListOf<Byte>()
	do {
	  val b = read().toByte()
	  if (b != CborBreak.byte) {
		bytes += b
	  }
	} while (b != CborBreak.byte)
	return bytes
  }

  fun counter() = CountingCborStreamMan(stream, this)
  fun storing() = ByteStoringStreamMan(stream, this)

}

class CountingCborStreamMan internal constructor(stream: InputStream, val parent: CborStreamManager):
  CborStreamManager(stream) {


  internal var numBytesRead = 0
	private set

  @Synchronized
  override fun read(): Int {
	numBytesRead += 1
	return super.read()
  }

  @Synchronized
  override fun readOrNullIfEOF(): Int? {
	return super.readOrNullIfEOF()?.also {
	  numBytesRead += 1
	}
  }

  @Synchronized override fun readNBytes(len: Int): ByteArray {
	numBytesRead += len
	return super.readNBytes(len) /*WARN: UNDEFINED BEHAVIOR IF EOF IS REACHED*/
  }

  override fun readUntilBreak() = NOT_IMPLEMENTED

}

class ByteStoringStreamMan internal constructor(stream: InputStream, val parent: CborStreamManager):
  CborStreamManager(stream) {


  internal var bytes = byteArrayOf()
	private set

  @Synchronized
  override fun read(): Int {
	return super.read().also {
	  bytes += it.toByte()
	}
  }

  @Synchronized
  override fun readOrNullIfEOF(): Int? {
	return super.readOrNullIfEOF()?.also {
	  bytes += it.toByte()
	}
  }

  @Synchronized override fun readNBytes(len: Int): ByteArray {
	return super.readNBytes(len).also {
	  bytes += it
	} /*WARN: UNDEFINED BEHAVIOR IF EOF IS REACHED*/
  }

  override fun readUntilBreak() = NOT_IMPLEMENTED

}