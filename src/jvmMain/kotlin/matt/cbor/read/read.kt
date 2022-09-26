package matt.cbor.read

import matt.cbor.log.INDENT
import matt.cbor.read.streamman.CborStreamManager
import matt.log.Logger
import matt.model.info.HasInfo
import matt.prim.str.times
import kotlin.contracts.contract

interface CborReadResult: HasInfo

object EOF: CborReadResult {
  override fun info() = "EOF"
}

typealias CborReader = CborReaderTyped<*>

abstract class CborReaderTyped<R: CborReadResult> {

  companion object {
	var defaultLogger: Logger? = null
  }

  protected var indent = 0
  @PublishedApi internal fun setIndentOf(reader: CborReader) {
	reader.indent = indent + 1
  }

  var logger: Logger? = defaultLogger

  open fun read(): R = readImpl().also {
	logger?.plusAssign(INDENT*indent + it.info())
  }

  protected abstract fun readImpl(): R
  private var streamMan: CborStreamManager? = null
  fun initStreamMan(newStreamMan: CborStreamManager) {
	require(!newStreamMan.isInitialized)
	newStreamMan.isInitialized = true
	streamMan = newStreamMan
  }

  @PublishedApi internal fun transferStreamTo(reader: CborReaderTyped<*>) {
	reader.streamMan = streamMan!!
	streamMan = null
  }

  @PublishedApi internal inline fun <RR, C: CborReader> lendStream(
	reader: C,
	andIndent: Boolean = false,
	op: C .()->RR
  ): RR {
	contract {
	  callsInPlace(op, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
	}
	transferStreamTo(reader)
	if (andIndent) {
	  setIndentOf(reader)
	}
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


