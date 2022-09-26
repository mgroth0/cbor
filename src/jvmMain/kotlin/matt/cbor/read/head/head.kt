package matt.cbor.read.head

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.head.InitialByte
import matt.cbor.err.NOT_WELL_FORMED
import matt.cbor.err.PARSER_BUG
import matt.cbor.read.CborReaderTyped
import matt.lang.pattern.lt

/*https://www.rfc-editor.org/rfc/rfc8949.html#section-3*/
class HeadReader(private val initialByte: InitialByte): CborReaderTyped<HeadWithArgument>() {
  private var didRead = false
  override fun readImpl(): HeadWithArgument {
	require(!didRead)
	didRead = true
	return when (initialByte.argumentCode.toInt()) {
	  in lt(24)            -> HeadWithArgument(initialByte)
	  24                   -> HeadWithArgument(initialByte, readNBytes(1))
	  25                   -> HeadWithArgument(initialByte, readNBytes(2))
	  26                   -> HeadWithArgument(initialByte, readNBytes(4))
	  27                   -> HeadWithArgument(initialByte, readNBytes(8))
	  28, 29, 30           -> NOT_WELL_FORMED
	  CBOR_UNLIMITED_COUNT -> HeadWithArgument(initialByte)
	  else                 -> PARSER_BUG
	}
  }

}

const val CBOR_UNLIMITED_COUNT = 31