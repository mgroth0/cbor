package matt.cbor.write

import matt.cbor.data.head.CBOR_UNLIMITED_ARRAY_INITIAL_BYTE
import matt.cbor.data.major.seven.CborBreak
import matt.cbor.writeAsCbor
import matt.file.MFile
import java.io.OutputStream

fun MFile.cborUnlimitedListWriter() = CborUnlimitedListWriter(outputStream())

@OptIn(ExperimentalUnsignedTypes::class)
class CborUnlimitedListWriter(@PublishedApi internal val outputStream: OutputStream) {

    fun startUnlimitedArray() {
        outputStream.write(byteArrayOf(CBOR_UNLIMITED_ARRAY_INITIAL_BYTE))
    }

    inline fun <reified T : Any> encodeAndWrite(o: T) = outputStream.writeAsCbor(o)

    fun writeBreak() {
        outputStream.write(ubyteArrayOf(CborBreak.uByte).toByteArray())
    }


    fun close() = outputStream.close()

}

//fun MFile.cborObjectWriter() = CborObjectWriter(outputStream())
//class CborObjectWriter(@PublishedApi internal val outputStream: OutputStream) {
//    fun startObject(numFields: Int) {
//        outputStream.write(
//            byteArrayOf(
//                InitialByte(
//                    majorType = MAP,
//                ).toByte()
//            )
//        )
//    }
//    fun close() = outputStream.close()
//}