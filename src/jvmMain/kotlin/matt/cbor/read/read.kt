package matt.cbor.read

import matt.cbor.read.streamman.CborStreamManager

interface CborReadResult
object EOF: CborReadResult

typealias CborReader = CborReaderTyped<*>

abstract class CborReaderTyped<R> {
  abstract fun read(): R
  private var streamMan: CborStreamManager? = null
  fun initStreamMan(newStreamMan: CborStreamManager) {
	require(!newStreamMan.isInitialized)
	newStreamMan.isInitialized = true
	streamMan = newStreamMan
	println("${this}.streamMan1 = $streamMan")
  }

  protected fun transferStreamTo(reader: CborReaderTyped<*>) {
	println("${this}.streamMan2 = $streamMan")
	println("${reader}.streamMan3 = $streamMan")
	reader.streamMan = streamMan!!
	streamMan = null
	println("${this}.streamMan4 = $streamMan")
	println("${reader}.streamMan5 = $streamMan")
  }

  protected fun <RR, C: CborReader> lendStream(reader: C, op: C.()->RR): RR {
	println("${this}.streamMan6 = $streamMan")
	println("${reader}.streamMan7 = $streamMan")
	transferStreamTo(reader)
	println("${this}.streamMan8 = $streamMan")
	println("${reader}.streamMan9 = $streamMan")
	val r = reader.op()
	println("${this}.streamMan10 = $streamMan")
	println("${reader}.streamMan11 = $streamMan")
	reader.transferStreamTo(this)
	println("${this}.streamMan12 = $streamMan")
	println("${reader}.streamMan13 = $streamMan")
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


