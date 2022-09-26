package matt.cbor.data.head

import matt.cbor.data.major.MajorType
import matt.cbor.data.major.MajorType.ARRAY
import matt.cbor.read.CborReadResult
import matt.cbor.read.head.CBOR_UNLIMITED_COUNT

interface HasInitialByte {
  val majorType: MajorType
  val argumentCode: Byte
}

/*https://www.rfc-editor.org/rfc/rfc8949.html#section-3*/
class InitialByte(
  override val majorType: MajorType,
  override val argumentCode: Byte
): HasInitialByte {
  constructor(initialByte: Int): this(
	majorType = MajorType.values()[initialByte shr 5],
	argumentCode = (initialByte and 0b000_11111).toByte()
  )

  fun toByte() = ((majorType.ordinal shl 5) and argumentCode.toInt()).toByte()
}

class HeadWithArgument(
  val initialByte: InitialByte,
  val extraBytes: ByteArray? = null
): HasInitialByte by initialByte, CborReadResult {

  override fun info() = "${majorType.label}($argumentCode${
	extraBytes?.let {
	  " + ${
		it.joinToString(prefix = "[", postfix = "]") {
		  it.toInt().toString()
		}
	  }"
	} ?: ""
  })"

  val numBytes = 1 + (extraBytes?.size ?: 0)
}

fun predictHeadSizeForCount(count: Int) = when {
  count <= 23     -> 1 + 0
  count <= 255    -> 1 + 1
  count <= 65_535 -> 1 + 2
  else            -> 1 + 4
} + count


val CBOR_UNLIMITED_ARRAY_INITIAL_BYTE by lazy {
  InitialByte(ARRAY, CBOR_UNLIMITED_COUNT.toByte()).toByte()
}