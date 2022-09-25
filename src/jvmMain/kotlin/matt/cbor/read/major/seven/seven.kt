package matt.cbor.read.major.seven

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.seven.CborDouble
import matt.cbor.data.major.seven.CborFalse
import matt.cbor.data.major.seven.CborFloat
import matt.cbor.data.major.seven.CborFloatOrSimpleValue
import matt.cbor.data.major.seven.CborNull
import matt.cbor.data.major.seven.CborTrue
import matt.cbor.data.major.seven.CborUndefined
import matt.cbor.err.NOT_WELL_FORMED
import matt.cbor.err.PARSER_BUG
import matt.cbor.read.major.MajorTypeReader
import java.nio.ByteBuffer

/*https://www.rfc-editor.org/rfc/rfc8949.html#name-floating-point-numbers-and-*/
class SpecialOrFloatReader(head: HeadWithArgument): MajorTypeReader<CborFloatOrSimpleValue>(head) {
  override fun read(): CborFloatOrSimpleValue {
	return when {
	  head.argumentCode <= 19 -> TODO()
	  else                    -> when (head.argumentCode.toInt()) {
		20         -> CborFalse
		21         -> CborTrue
		22         -> CborNull
		23         -> CborUndefined
		24         -> TODO()
		25         -> TODO()
		26         -> CborFloat(ByteBuffer.wrap(head.extraBytes!!).float)
		27         -> CborDouble(ByteBuffer.wrap(head.extraBytes!!).double)
		28, 29, 30 -> NOT_WELL_FORMED
		31         -> PARSER_BUG  /*since Break is not a data item it should be handled separately by the reader expecting it*/
		else       -> PARSER_BUG
	  }
	}
  }
}