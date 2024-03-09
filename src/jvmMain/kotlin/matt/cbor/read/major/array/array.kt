package matt.cbor.read.major.array

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.CborDataItem
import matt.cbor.data.major.array.CborArray
import matt.cbor.data.major.seven.CborBreak
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.item.CborItemReader
import matt.cbor.read.item.MightBeBreak
import matt.cbor.read.item.isNotBreak
import matt.cbor.read.major.IntArgTypeReader
import matt.cbor.read.major.MajorTypeReader
import matt.lang.common.NOT_IMPLEMENTED

class ArrayReader(head: HeadWithArgument): IntArgTypeReader<CborArray<*>>(head) {


    override fun readImpl() = CborArray(readAsSequence().toList())

    fun readAsSequence(): Sequence<CborDataItem<*>> = readAsSequenceBase { read() }


    override fun readAndStoreBytes(): CborReadResultWithBytes<CborArray<*>> {


        val itemsWithBytes = readAsSequenceWithBytes().toList()
        return CborReadResultWithBytes(
            CborArray(itemsWithBytes.map { it.result }),
            run {
                var bb =
                    if (itemsWithBytes.isEmpty()) byteArrayOf() else itemsWithBytes.map { it.bytes }
                        .reduce { acc, bytes -> acc + bytes }
                if (!hasCount) bb += CborBreak.byte
                bb
            }
        )
    }


    fun readAsSequenceWithBytes(): Sequence<CborReadResultWithBytes<CborDataItem<*>>> =
        readAsSequenceBase { readAndStoreBytes() }

    private inline fun <R: MightBeBreak> readAsSequenceBase(crossinline readOp: CborItemReader.() -> R): Sequence<R> =
        sequence {
            if (hasCount) range.map {
                lendStream(CborItemReader()) {
                    yield(readOp())
                }
            } else {
                do {
                    val item =
                        lendStream(CborItemReader()) {
                            readOp()
                        }
                    if (item.isNotBreak) yield(item)
                } while (item.isNotBreak)
            }
        }

    inline fun <reified RD: MajorTypeReader<*>, R> readEachManually(op: RD.() -> R): List<R> {
        if (!hasCount) NOT_IMPLEMENTED
        return range.map {
            lendStream(CborItemReader()) {
                readManually<RD, R> { op() }
            }
        }
    }
}
