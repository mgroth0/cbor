package matt.cbor.read.major

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.CborDataItem
import matt.cbor.err.NOT_WELL_FORMED
import matt.cbor.err.PARSER_BUG
import matt.cbor.read.CborReaderTyped
import java.nio.ByteBuffer


abstract class MajorTypeReader<D: CborDataItem<*>>(val head: HeadWithArgument): CborReaderTyped<D>()

abstract class IntArgTypeReader<D: CborDataItem<*>>(head: HeadWithArgument): MajorTypeReader<D>(head) {
  val argumentValue = when {
	head.argumentCode < 24 -> head.argumentCode.toUByte()
	else                   -> when (head.argumentCode.toInt()) {
	  24         -> ByteBuffer.wrap(head.extraBytes!!).get().toUByte()
	  25         -> ByteBuffer.wrap(head.extraBytes!!).short.toUShort()
	  26         -> ByteBuffer.wrap(head.extraBytes!!).int.toUInt()
	  27         -> ByteBuffer.wrap(head.extraBytes!!).long.toULong()
	  28, 29, 30 -> NOT_WELL_FORMED
	  31         -> null
	  else       -> PARSER_BUG
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
	  else      -> PARSER_BUG
	}
  }
  val range by lazy {
	0L.toULong() until count
  }
}

