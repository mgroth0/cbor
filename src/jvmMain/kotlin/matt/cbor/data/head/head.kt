package matt.cbor.data.head

import matt.cbor.data.major.MajorType

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
}

class HeadWithArgument(
  val initialByte: InitialByte,
  val extraBytes: ByteArray? = null
): HasInitialByte by initialByte

fun predictHeadSizeForCount(count: Int) = when {
  count <= 23     -> 1 + 0
  count <= 255    -> 1 + 1
  count <= 65_535 -> 1 + 2
  else            -> 1 + 4
} + count
