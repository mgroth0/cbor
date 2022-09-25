package matt.cbor.read.major.array

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.array.CborArray
import matt.cbor.read.major.IntArgTypeReader

class ArrayReader(head: HeadWithArgument): IntArgTypeReader<CborArray>(head) {
  override fun read(): CborArray {
	return argumentValue?.let { len ->
	  CborArray((0.toULong()..len).map {
		CborItemReader().withStream {
		  read()
		}
	  })
	} ?: run {
	  TODO()
	}
  }
}