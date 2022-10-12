package matt.cbor.read.major.bytestr

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.bytestr.CborByteString
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.major.IntArgTypeReader

class ByteStringReader(head: HeadWithArgument): IntArgTypeReader<CborByteString>(head) {
  override fun readImpl(): CborByteString {
	return argumentValue?.let {
	  CborByteString(readNBytes(count).toList())
	} ?: run {

	  /*https://www.rfc-editor.org/rfc/rfc8949.html#section-3.2.3*/
	  TODO(
		"""
		Indefinite-length strings are represented by a byte containing the major type for byte string or text string with an additional information value of 31, followed by a series of zero or more strings of the specified type ("chunks") that have definite lengths, and finished by the "break" stop code (Section 3.2.1).
	  """.trimIndent()
	  )
	}
  }

  override fun readAndStoreBytes(): CborReadResultWithBytes<CborByteString> {
	val bytes = readNBytes(count)

	return CborReadResultWithBytes(
	  CborByteString(bytes.toList()),
	  bytes
	)
  }


}