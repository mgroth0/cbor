package matt.cbor.read.major.txtstr

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.txtstr.CborTextString
import matt.cbor.read.major.IntArgTypeReader

class TextStringReader(head: HeadWithArgument): IntArgTypeReader<CborTextString>(head) {
  override fun read(): CborTextString {
	return argumentValue?.let {
	  CborTextString(readNBytes(count).decodeToString())
	} ?: run {

	  /*https://www.rfc-editor.org/rfc/rfc8949.html#section-3.2.3*/
	  TODO(
		"""
		Indefinite-length strings are represented by a byte containing the major type for byte string or text string with an additional information value of 31, followed by a series of zero or more strings of the specified type ("chunks") that have definite lengths, and finished by the "break" stop code (Section 3.2.1).
	  """.trimIndent()
	  )


	}
  }
}