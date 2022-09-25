package matt.cbor

import matt.cbor.CborArrayReader.Companion.unlimitedArrayBreakInt
import matt.cbor.CborStreamReader.CborBreak
import matt.cbor.MajorType.ARRAY
import matt.cbor.MajorType.BYTE_STRING
import matt.cbor.MajorType.MAP
import matt.cbor.MajorType.N_INT
import matt.cbor.MajorType.POS_OR_U_INT
import matt.cbor.MajorType.SPECIAL_OR_FLOAT
import matt.cbor.MajorType.TAG
import matt.cbor.MajorType.TEXT_STRING
import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.reflect.KClass

/*https://cbor.io/spec.html*//*https://www.wikiwand.com/en/CBOR*/


enum class MajorType(val readReturnType: KClass<*>) {
  POS_OR_U_INT(Unit::class),
  N_INT(Unit::class),
  BYTE_STRING(Unit::class),
  TEXT_STRING(Unit::class),
  ARRAY(CborArrayReader::class),
  MAP(CborMapReader::class),
  TAG(Unit::class),
  SPECIAL_OR_FLOAT(Unit::class)
}


class CborStreamReader(
  stream: InputStream
) {
  val stream = (stream as? BufferedInputStream) ?: stream.buffered()

  fun nextDataItem(): Any? {
	val b = stream.read()
	println("got data item byte: $b")
	val majorType = MajorType.values()[b shr 5]
	println("\tmajorType=$majorType")
	val argumentCode = b and 0b000_11111
	val argumentValue: Any? = when {
	  argumentCode < 24 -> argumentCode
	  else              -> when (argumentCode) {
		24   -> ByteBuffer.wrap(byteArrayOf(0b0, 0b0, 0b0) + stream.readNBytes(1)).run {
		  when (majorType) {
			ARRAY, MAP, POS_OR_U_INT, N_INT, TEXT_STRING, BYTE_STRING -> int
			else                                                      -> TODO()
		  }
		}

		25   -> ByteBuffer.wrap(byteArrayOf(0b0, 0b0) + stream.readNBytes(2)).run {
		  when (majorType) {
			ARRAY, MAP, POS_OR_U_INT, N_INT, TEXT_STRING, BYTE_STRING -> int
			else                                                      -> TODO()
		  }
		}

		26   -> TODO()
		27   -> ByteBuffer.wrap(stream.readNBytes(8)).run {
		  when (majorType) {
			ARRAY, MAP, POS_OR_U_INT, N_INT, TEXT_STRING, BYTE_STRING -> int
			SPECIAL_OR_FLOAT                                          -> double
			else                                                      -> TODO()
		  }
		}

		28   -> TODO()
		29   -> TODO()
		30   -> TODO()
		31   -> when (majorType) {
		  POS_OR_U_INT     -> NOT_WELL_FORMED
		  N_INT            -> NOT_WELL_FORMED
		  BYTE_STRING      -> null
		  TEXT_STRING      -> null
		  ARRAY            -> null
		  MAP              -> null
		  TAG              -> NOT_WELL_FORMED
		  SPECIAL_OR_FLOAT -> CborBreak
		}

		else -> PARSER_BUG
	  }
	}


	println("\targumentCode=$argumentCode")
	println("\targumentValue=$argumentValue")
	return when (majorType) {
	  POS_OR_U_INT             -> argumentValue
	  N_INT                    -> -1 - (argumentValue as Int)
	  TEXT_STRING, BYTE_STRING -> {
		(if (argumentValue != null) {
		  stream.readNBytes(argumentValue as Int).let {
			when (majorType) {
			  TEXT_STRING -> it.decodeToString()
			  BYTE_STRING -> it
			  else        -> PARSER_BUG
			}
		  }
		} else {
		  when (majorType) {
			TEXT_STRING -> {
			  val s = StringBuilder()
			  do {
				val c = stream.read()
				if (c != unlimitedArrayBreakInt) {
				  s.append(c)
				}
			  } while (c != unlimitedArrayBreakInt)
			  s
			}

			BYTE_STRING -> TODO()
			else        -> PARSER_BUG
		  }

		})
	  }

	  ARRAY                    -> CborArrayReader(
		this,
		argumentValue as Int?,
		numHeaderBytes = when {
		  argumentCode < 24 -> 1
		  else              -> when (argumentCode) {
			24   -> 1 + 1
			25   -> 1 + 2
			27   -> 1 + 8
			else -> TODO()
		  }
		}
	  )

	  MAP                      -> CborMapReader(
		this,
		argumentValue as Int?
	  )

	  TAG                      -> TODO()
	  SPECIAL_OR_FLOAT         -> when (argumentCode) {
		22   -> null
		27   -> argumentValue
		31   -> CborBreak
		else -> TODO()
	  }
	}
  }

  object CborBreak

  inline fun streamMap(op: CborMapReader.()->Unit) = (nextDataItem() as CborMapReader).op()
  inline fun streamArray(op: CborArrayReader.()->Unit) = (nextDataItem() as CborArrayReader).op()
}


fun numCborBytesFor(byteArray: ByteArray) = byteArray.size.run {
  when {
	this <= 23     -> 1 + 0
	this <= 255    -> 1 + 1
	this <= 65_535 -> 1 + 2
	else           -> 1 + 4
  } + this
}

class CborByteArray(
  val b: ByteArray,
  val numHeaderBytes: Int
)

class CborArrayReader(
  private val stream: CborStreamReader,
  val length: Int?,
  val numHeaderBytes: Int
) {
  companion object {
	const val unlimitedArrayStart = 0b100_11111.toByte()
	const val unlimitedArrayBreak = 255.toByte()
	const val unlimitedArrayBreakInt = unlimitedArrayBreak.toInt()
  }

  private var nextIndex = 0
  private var hitBreak = false

  fun nextElement(): Any? {
	if (length != null) require(nextIndex < length) else require(!hitBreak)
	return stream.nextDataItem()
  }

  fun forEachElement(op: (Any?)->Unit) {
	if (length != null) {
	  repeat(length) {
		op(nextElement())
	  }
	} else {
	  do {
		val e = nextElement()
		if (e != CborBreak) op(e)
	  } while (e != CborBreak)
	}
	//	println("finished forEachElement")
  }


  fun numBytesIfListOfDoublesIncludingHeader(): Int {
	require(length != null)
	return numHeaderBytes + length*9
  }

  fun numBytesIfListOfFloat32sIncludingHeader(): Int {
	require(length != null)
	return numHeaderBytes + length*5
  }

  //  fun numBytesIfListOfBytesIncludingHeader(): Int {
  //	require(length != null)
  //	return numHeaderBytes + length
  //  }


  fun readAllElements(): List<Any?> {
	val list = mutableListOf<Any?>()
	forEachElement {
	  list += it
	}
	return list
  }

  fun <R> mapElements(op: (Any?)->R): List<R> {
	val list = mutableListOf<R>()
	forEachElement {
	  list += op(it)
	}
	return list
  }

  fun readSetSizeDoubleArray(): List<Double> {
	return (0 until length!!).map {
	  stream.stream.skipNBytes(1)
	  ByteBuffer.wrap(stream.stream.readNBytes(8)).double
	}
  }

  fun readSetSizeFloat32Array(): List<Float> {
	return (0 until length!!).map {
	  stream.stream.skipNBytes(1)
//	  val x = stream.stream.read()
//	  require(x == 0b10111010) {
//		"expected 0b10111010 but got $x"
//	  }
	  ByteBuffer.wrap(stream.stream.readNBytes(4)).float
	}
  }

  //  fun readSetSizeByteArrayAsInt8s(): IntArray {
  //	val r = IntArray(length!!)
  //	ByteBuffer.wrap(stream.stream.readNBytes(length)).asIntBuffer().get(r)
  //	return r
  //  }

}

class CborMapReader(
  private val stream: CborStreamReader,
  val length: Int?
) {
  fun nextPair(): Pair<Any?, Any?> {
	return stream.nextDataItem() to stream.nextDataItem()
  }

  fun nextValue(requireKeyIs: String): Any? {
	nextPair().apply {
	  require(first == requireKeyIs) {
		"expected key \"$requireKeyIs\" but got \"$first\""
	  }
	  return second
	}
  }
}

private val PARSER_BUG: Nothing get() = throw RuntimeException("Problem on the cbor parser end")
private val NOT_WELL_FORMED: Nothing get() = throw CborParseException()

class CborParseException: Exception()


private fun InputStream.readLong(): Long {
  var result = 0L
  for (i in 0..7) {
	result = (result shl 8) or read().toLong()
  }
  return result
}

fun iDoUseCborLibButOnlyInlineOrConst() = println("iDoUseCborLibButOnlyInlineOrConst")