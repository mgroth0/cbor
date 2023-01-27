package matt.cbor.read.major

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.CborDataItem
import matt.cbor.err.NOT_WELL_FORMED
import matt.cbor.err.UnexpectedCountException
import matt.cbor.err.UnexpectedCountNotInRangeException
import matt.cbor.err.parserBug
import matt.cbor.read.CborReaderTyped
import java.nio.ByteBuffer


abstract class MajorTypeReader<D: CborDataItem<*>>(val head: HeadWithArgument): CborReaderTyped<D>()

@OptIn(ExperimentalStdlibApi::class)
abstract class IntArgTypeReader<D: CborDataItem<*>>(head: HeadWithArgument): MajorTypeReader<D>(head) {
  val argumentValue = when {
	head.argumentCode < 24 -> head.argumentCode.toUByte()
	else                   -> when (val code = head.argumentCode.toInt()) {
	  24         -> ByteBuffer.wrap(head.extraBytes!!).get().toUByte()
	  25         -> ByteBuffer.wrap(head.extraBytes!!).short.toUShort()
	  26         -> ByteBuffer.wrap(head.extraBytes!!).int.toUInt()
	  27         -> ByteBuffer.wrap(head.extraBytes!!).long.toULong()
	  28, 29, 30 -> NOT_WELL_FORMED
	  31         -> null
	  else       -> parserBug("argumentCode=${code}, which should never happen")
	}
  }
  val hasCount = argumentValue != null
  val count by lazy {
	when (argumentValue) {
	  is ULong  -> 0L.toULong() + argumentValue
	  is UInt   -> 0.toUInt().toULong() + argumentValue
	  is UShort -> 0.toUShort().toULong() + argumentValue
	  is UByte  -> 0.toUByte().toULong() + argumentValue
	  is Byte   -> 0b0.toULong() + argumentValue.toUByte()
	  null      -> parserBug("tried to get count when hasCount=$hasCount")
	  else      -> parserBug("argumentValue is ${argumentValue::class}, which should never happen")
	}
  }

  fun expectCount(expected: ULong) {
	if (count != expected) {
	  throw UnexpectedCountException(expected = expected, received = count)
	}
  }
  fun expectCount(range: ULongRange) {
	if (count !in range) {
	  throw UnexpectedCountNotInRangeException(expected = range, received = count)
	}
  }

  val range by lazy {
	0L.toULong() ..< count
  }
}

