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
import matt.model.info.HasInfo
import kotlin.reflect.KClass

interface CborDataItem<T>: CborReadResult, HasInfo {
  val raw: T
}

/*https://www.rfc-editor.org/rfc/rfc8949.html#name-major-types*/
enum class MajorType(private val readerCls: KClass<out MajorTypeReader<*>>, val label: String) {
  POS_OR_U_INT(PosOrUIntReader::class, "int"),
  N_INT(NegIntReader::class, "-int"),
  BYTE_STRING(ByteStringReader::class, "bytes"),
  TEXT_STRING(TextStringReader::class, "text"),
  ARRAY(ArrayReader::class, "array"),
  MAP(MapReader::class, "map"),
  TAG(TagReader::class, "tag"),
  SPECIAL_OR_FLOAT(SpecialOrFloatReader::class, "special/float");

  fun reader(headWithArgument: HeadWithArgument): MajorTypeReader<*> {
	return readerCls.constructors.first().call(headWithArgument)
  }
}


