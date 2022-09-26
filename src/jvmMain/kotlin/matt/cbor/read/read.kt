package matt.cbor.read

import matt.cbor.log.INDENT
import matt.cbor.read.streamman.CborStreamManager
import matt.log.Logger
import matt.model.info.HasInfo
import matt.prim.str.times
import matt.prim.str.truncateWithElipses
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

  @PublishedApi
  internal var indent = 0


  var logger: Logger? = defaultLogger

  open fun printReadInfo(r: R) = logger?.log(INDENT*(indent-1) + r.info().truncateWithElipses(25))

  open fun read(): R = readImpl().also {
	printReadInfo(it)
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
	op: C .()->RR
  ): RR {
	contract {
	  callsInPlace(op, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
	}
	transferStreamTo(reader)
	reader.indent = indent + 1
	val r = reader.op()
	reader.transferStreamTo(this)
	return r
  }

  protected fun readByte() = streamMan!!.read().also { require(it > -1) }
  fun readNBytes(len: Int) = streamMan!!.readNBytes(len)
  protected fun readNBytes(len: ULong): ByteArray {
	require(len <= Int.MAX_VALUE.toUInt())
	return readNBytes(len.toInt())
  }

  protected fun readOrNullIfEOF() = streamMan!!.readOrNullIfEOF()
  protected fun readUntilBreak() {
	streamMan
  }
}


