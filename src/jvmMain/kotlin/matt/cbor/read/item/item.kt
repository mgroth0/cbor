package matt.cbor.read.item

import matt.cbor.data.head.InitialByte
import matt.cbor.data.major.CborDataItem
import matt.cbor.err.UnexpectedMajorTypeException
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.CborReaderTyped
import matt.cbor.read.head.HeadReader
import matt.cbor.read.major.MajorTypeReader
import matt.lang.anno.SeeURL
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

class CborItemReader : CborReaderTyped<CborDataItem<*>>() {

    /*the payload itself already prints log info in its own read()*/
    override fun read() = readImpl()

    override fun readImpl(): CborDataItem<*> =
        readManually<MajorTypeReader<*>, CborDataItem<*>> {
            val r = read()
            r
        }

    @PublishedApi
    internal fun readHead() =
        run {
            val initialByte = InitialByte(readByte())
            val headReader = HeadReader(initialByte)
            lendStream(headReader) {
                read()
            }
        }


    @SeeURL("https://youtrack.jetbrains.com/issue/KT-65158/K2-Contracts-False-positive-WRONGINVOCATIONKIND-with-unrelated-higher-order-function-call")
    @Suppress("WRONG_INVOCATION_KIND")
    inline fun <reified RD : MajorTypeReader<*>, R> readManually(op: RD.() -> R): R {
        contract {
            callsInPlace(op, EXACTLY_ONCE)
        }
        val head = readHead()


        val payloadReader =
            head.majorType.reader(head) as? RD ?: throw UnexpectedMajorTypeException(
                expected = RD::class,
                received = head.majorType
            )
        return lendStream(payloadReader) {
            op()
        }
    }

    override fun readAndStoreBytes(): CborReadResultWithBytes<CborDataItem<*>> {


        val initialByte = InitialByte(readByte())
        val headReader = HeadReader(initialByte)
        val headWithBytes =
            lendStream(headReader) {
                readAndStoreBytes()
            }
        val payloadReader = headWithBytes.result.majorType.reader(headWithBytes.result)


        val itemWIthBytes =
            lendStream(payloadReader) {
                readAndStoreBytes()
            }

        return CborReadResultWithBytes(
            itemWIthBytes.result,
            headWithBytes.bytes + itemWIthBytes.bytes
        )
    }
}

interface MightBeBreak {
    val isBreak: Boolean
}

val MightBeBreak.isNotBreak get() = !isBreak
