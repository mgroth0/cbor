package matt.cbor.read.major

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.CborDataItem
import matt.cbor.err.NOT_WELL_FORMED
import matt.cbor.err.PARSER_BUG
import matt.cbor.read.CborReaderTyped
import java.nio.ByteBuffer


abstract class MajorTypeReader<D: CborDataItem>(protected val head: HeadWithArgument): CborReaderTyped<D>()

abstract class IntArgTypeReader<D: CborDataItem>(head: HeadWithArgument): MajorTypeReader<D>(head) {
  val argumentValue = when {
	head.argumentCode < 24 -> head.argumentCode.toLong().toULong()
	else                   -> when (head.argumentCode.toInt()) {
	  24         -> ByteBuffer.wrap(head.extraBytes!!).get()
	  25         -> ByteBuffer.wrap(head.extraBytes!!).short
	  26         -> ByteBuffer.wrap(head.extraBytes!!).int
	  27         -> ByteBuffer.wrap(head.extraBytes!!).long
	  28, 29, 30 -> NOT_WELL_FORMED
	  31         -> null
	  else       -> PARSER_BUG
	}?.toLong()?.toULong()
  }
}