package matt.cbor.read.head

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.head.InitialByte
import matt.cbor.data.major.MajorType.ARRAY
import matt.cbor.data.major.MajorType.BYTE_STRING
import matt.cbor.data.major.MajorType.MAP
import matt.cbor.data.major.MajorType.N_INT
import matt.cbor.data.major.MajorType.POS_OR_U_INT
import matt.cbor.data.major.MajorType.SPECIAL_OR_FLOAT
import matt.cbor.data.major.MajorType.TAG
import matt.cbor.data.major.MajorType.TEXT_STRING
import matt.cbor.err.NOT_WELL_FORMED
import matt.cbor.err.parserBug
import matt.cbor.log.INDENT
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.CborReaderTyped
import matt.lang.assertions.require.requireNot
import matt.lang.pattern.lt
import matt.prim.str.times

/*https://www.rfc-editor.org/rfc/rfc8949.html#section-3*/
class HeadReader(private val initialByte: InitialByte) : CborReaderTyped<HeadWithArgument>() {
    private var didRead = false


    override fun readImpl(): HeadWithArgument {
        requireNot(didRead)
        didRead = true
        return when (val code = initialByte.argumentCode.toInt()) {
            in lt(24)            -> HeadWithArgument(initialByte)
            24                   -> HeadWithArgument(initialByte, readNBytes(1))
            25                   -> HeadWithArgument(initialByte, readNBytes(2))
            26                   -> HeadWithArgument(initialByte, readNBytes(4))
            27                   -> HeadWithArgument(initialByte, readNBytes(8))
            28, 29, 30           -> NOT_WELL_FORMED
            CBOR_UNLIMITED_COUNT -> HeadWithArgument(initialByte)
            else                 -> parserBug("argumentCode=$code, which should never happen")
        }
    }

    override fun printReadInfo(r: HeadWithArgument) {

        if (logger == null) return

        val anno = INDENT * (indent - 1) + r.info()

        when (initialByte.majorType) {
            SPECIAL_OR_FLOAT                                   -> Unit
            POS_OR_U_INT, N_INT, BYTE_STRING, TEXT_STRING, TAG -> logger?.print("$anno: ")

            ARRAY, MAP                                         -> logger?.log(anno)
        }
    }


    override fun readAndStoreBytes(): CborReadResultWithBytes<HeadWithArgument> {
        val head = readImpl()
        val headBytes = byteArrayOf(head.initialByte.toByte()) + (head.extraBytes ?: byteArrayOf())
        return CborReadResultWithBytes(head, headBytes)
    }
}

const val CBOR_UNLIMITED_COUNT = 31
