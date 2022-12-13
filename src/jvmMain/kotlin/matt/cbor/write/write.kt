package matt.cbor.write

import matt.cbor.data.head.CBOR_UNLIMITED_ARRAY_INITIAL_BYTE
import matt.cbor.data.major.seven.CborBreak
import matt.cbor.writeAsCbor
import matt.file.MFile
import java.io.OutputStream

fun MFile.cborWriter() = CborWriter(outputStream())

@OptIn(ExperimentalUnsignedTypes::class)
class CborWriter(@PublishedApi internal val outputStream: OutputStream) {

  fun startUnlimitedArray() {
	outputStream.write(byteArrayOf(CBOR_UNLIMITED_ARRAY_INITIAL_BYTE))
  }

  inline fun <reified T: Any> encodeAndWrite(o: T) = outputStream.writeAsCbor(o)

  fun writeBreak() {
	outputStream.write(ubyteArrayOf(CborBreak.uByte).toByteArray())
  }


  fun close() = outputStream.close()

}