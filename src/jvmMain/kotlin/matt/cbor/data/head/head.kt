package matt.cbor.data.head

import matt.cbor.data.major.MajorType
import matt.cbor.data.major.MajorType.ARRAY
import matt.cbor.data.major.MajorType.SPECIAL_OR_FLOAT
import matt.cbor.data.major.seven.CborBreak
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

  fun toByte() = ((majorType.ordinal shl 5) or argumentCode.toInt()).toByte()
}

class HeadWithArgument(
  val initialByte: InitialByte,
  val extraBytes: ByteArray? = null
): HasInitialByte by initialByte, CborReadResult {

  override fun info(): String {

	return when {
	  isBreak -> "BREAK"
	  else    -> {
		val argumentString = when {
		  argumentCode.toInt() == 31 -> "?"
		  else                       -> argumentCode.toString()
		}

		val extraBytesString = extraBytes?.let {
		  " + ${
			it.joinToString(prefix = "[", postfix = "]") {
			  it.toInt().toString()
			}
		  }"
		} ?: ""

		"${majorType.label}($argumentString${extraBytesString})"
		/*when {
		  majorType == SPECIAL_OR_FLOAT -> ""*//*SpecialOrFloatReader(this).readWithoutPrinting().infoString()*//*
		  else                          -> "${majorType.label}($argumentString${extraBytesString})"
		}*/


	  }
	}


  }

  override val isBreak: Boolean
	get() {
	  if (this.majorType != SPECIAL_OR_FLOAT) return false
	  return argumentCode == CborBreak.argumentCodeByte
	}

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