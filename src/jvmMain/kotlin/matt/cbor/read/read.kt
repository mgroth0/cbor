@file:OptIn(ExperimentalContracts::class)

package matt.cbor.read

import matt.cbor.log.INDENT
import matt.cbor.read.item.MightBeBreak
import matt.cbor.read.streamman.ByteStoringStreamMan
import matt.cbor.read.streamman.CborStreamManager
import matt.cbor.read.streamman.CountingCborStreamMan
import matt.lang.assertions.require.requireGreaterThan
import matt.lang.assertions.require.requireLessThanOrEqualTo
import matt.lang.assertions.require.requireNot
import matt.lang.tostring.SimpleStringableClass
import matt.log.logger.Logger
import matt.model.obj.info.HasInfo
import matt.prim.byte.reasonablePrintableString
import matt.prim.str.times
import matt.prim.str.truncateWithElipses
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface CborReadResult : HasInfo, MightBeBreak

object EOF : CborReadResult {
    override fun info() = "EOF"
    override val isBreak = false
}

typealias CborReader = CborReaderTyped<*>

abstract class CborReaderTyped<R : CborReadResult> {

    companion object {
        var defaultLogger: Logger? = null
    }

    @PublishedApi
    internal var indent = 0


    var logger: Logger? = defaultLogger

    open fun printReadInfo(r: R) {
        logger?.log(INDENT * (indent - 1) + r.info().truncateWithElipses(25))
    }

    fun readWithoutPrinting() = readImpl()
    open fun read(): R =
        readImpl().also {
            printReadInfo(it)
        }

    abstract fun readAndStoreBytes(): CborReadResultWithBytes<out R>

    protected abstract fun readImpl(): R
    private var streamMan: CborStreamManager? = null
    fun initStreamMan(newStreamMan: CborStreamManager) {
        requireNot(newStreamMan.isInitialized)
        newStreamMan.isInitialized = true
        streamMan = newStreamMan
    }

    @PublishedApi
    internal fun transferStreamTo(reader: CborReaderTyped<*>) {
        reader.streamMan = streamMan!!
        streamMan = null
    }

    @PublishedApi
    internal inline fun <RR, C : CborReader> lendStream(
        reader: C,
        op: C.() -> RR
    ): RR {
        contract {
            callsInPlace(op, InvocationKind.EXACTLY_ONCE)
        }
        transferStreamTo(reader)
        reader.indent = indent + 1
        val r = reader.op()
        reader.transferStreamTo(this)
        return r
    }

    protected fun readByte() =
        streamMan!!.read().also {
            requireGreaterThan(it, -1) {
                "unexpectedly reached end of stream"
            }
        }

    fun readNBytes(len: Int) = streamMan!!.readNBytes(len)
    protected fun readNBytes(len: ULong): ByteArray {
        requireLessThanOrEqualTo(len, Int.MAX_VALUE.toUInt())
        return readNBytes(len.toInt())
    }

    protected fun readOrNullIfEOF() = streamMan!!.readOrNullIfEOF()
    protected fun readUntilBreak() {
        streamMan
    }

    internal fun initByteCounter() {
        streamMan = streamMan!!.counter()
    }

    internal fun endByteCounter(): Int {
        val counter = (streamMan as CountingCborStreamMan)
        streamMan = counter.parent
        return counter.numBytesRead
    }

    internal fun initByteStoring() {
        streamMan = streamMan!!.storing()
    }

    internal fun endByteStoring(): ByteArray {
        val counter = (streamMan as ByteStoringStreamMan)
        streamMan = counter.parent
        return counter.bytes
    }
}


class CborReadResultWithBytes<R : CborReadResult>(
    val result: R,
    val bytes: ByteArray
) : SimpleStringableClass(), MightBeBreak by result {
    override fun toStringProps() =
        mapOf(
            "bytes" to bytes.reasonablePrintableString(),
            "result" to result
        )
}

fun <RD : CborReaderTyped<R>, R : CborReadResult, RR> RD.withByteCounter(op: RD.() -> RR): Pair<RR, Int> {
    initByteCounter()
    val r = op()
    val count = endByteCounter()
    return r to count
}

fun <RD : CborReaderTyped<R>, R : CborReadResult, RR> RD.withByteStoring(op: RD.() -> RR): Pair<RR, ByteArray> {
    initByteStoring()
    val r = op()
    val bytes = endByteStoring()
    return r to bytes
}
