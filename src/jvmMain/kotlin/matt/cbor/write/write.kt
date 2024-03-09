package matt.cbor.write

import matt.cbor.data.head.CBOR_UNLIMITED_ARRAY_INITIAL_BYTE
import matt.cbor.data.major.seven.CborBreak
import matt.cbor.writeAsCbor
import matt.file.JioFile
import java.io.OutputStream
import kotlin.io.path.outputStream

fun JioFile.cborUnlimitedListWriter() = CborUnlimitedListWriter(outputStream())

@OptIn(ExperimentalUnsignedTypes::class)
class CborUnlimitedListWriter(
    @PublishedApi internal val outputStream: OutputStream
) {

    fun startUnlimitedArray() {
        outputStream.write(byteArrayOf(CBOR_UNLIMITED_ARRAY_INITIAL_BYTE))
    }

    inline fun <reified T : Any> encodeAndWrite(o: T) = outputStream.writeAsCbor(o)

    fun writeBreak() {
        outputStream.write(ubyteArrayOf(CborBreak.uByte).toByteArray())
    }


    fun close() = outputStream.close()
}
