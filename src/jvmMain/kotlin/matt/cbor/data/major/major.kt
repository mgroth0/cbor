package matt.cbor.data.major

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.read.CborReadResult
import matt.cbor.read.major.MajorTypeReader
import matt.cbor.read.major.array.ArrayReader
import matt.cbor.read.major.bytestr.ByteStringReader
import matt.cbor.read.major.map.MapReader
import matt.cbor.read.major.nint.NegIntReader
import matt.cbor.read.major.seven.SpecialOrFloatReader
import matt.cbor.read.major.tag.TagReader
import matt.cbor.read.major.txtstr.TextStringReader
import matt.cbor.read.major.uint.PosOrUIntReader
import kotlin.reflect.KClass

interface CborDataItem: CborReadResult

/*https://www.rfc-editor.org/rfc/rfc8949.html#name-major-types*/
enum class MajorType(private val readerCls: KClass<out MajorTypeReader<*>>) {
  POS_OR_U_INT(PosOrUIntReader::class),
  N_INT(NegIntReader::class),
  BYTE_STRING(ByteStringReader::class),
  TEXT_STRING(TextStringReader::class),
  ARRAY(ArrayReader::class),
  MAP(MapReader::class),
  TAG(TagReader::class),
  SPECIAL_OR_FLOAT(SpecialOrFloatReader::class);

  fun reader(headWithArgument: HeadWithArgument): MajorTypeReader<*> {
	return readerCls.constructors.first().call(headWithArgument)
  }
}


