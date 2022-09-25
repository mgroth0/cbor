package matt.cbor.read

import matt.cbor.read.streamman.CborStreamManager
import matt.log.Logger

interface CborReadResult
object EOF: CborReadResult

typealias CborReader = CborReaderTyped<*>

abstract class CborReaderTyped<R> {

  companion object {
	var defaultLogger: Logger? = null
  }

  internal var indent = 0

  var logger: Logger? = defaultLogger

  abstract fun read(): R
  private var streamMan: CborStreamManager? = null
  fun initStreamMan(newStreamMan: CborStreamManager) {
	require(!newStreamMan.isInitialized)
	newStreamMan.isInitialized = true
	streamMan = newStreamMan
  }

  protected fun transferStreamTo(reader: CborReaderTyped<*>) {
	reader.streamMan = streamMan!!
	streamMan = null
  }

  protected fun <RR, C: CborReader> lendStream(reader: C, op: C.()->RR): RR {
	transferStreamTo(reader)
	val r = reader.op()
	reader.transferStreamTo(this)
	return r
  }

  protected fun readByte() = streamMan!!.read().also { require(it > -1) }
  protected fun readNBytes(len: Int) = streamMan!!.readNBytes(len)
  protected fun readNBytes(len: ULong): ByteArray {
	require(len <= Int.MAX_VALUE.toUInt())
	return readNBytes(len.toInt())
  }

  protected fun readOrNullIfEOF() = streamMan!!.readOrNullIfEOF()
  protected fun readUntilBreak() {
	streamMan
  }
}


