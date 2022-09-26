package matt.cbor.read.major.array

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.array.CborArray
import matt.cbor.read.major.IntArgTypeReader
import matt.cbor.read.major.MajorTypeReader

class ArrayReader(head: HeadWithArgument): IntArgTypeReader<CborArray<*>>(head) {
  override fun readImpl(): CborArray<*> {
	return argumentValue?.let {
	  CborArray(range.map {
		lendStream(CborItemReader(), andIndent = true) {
		  read()
		}
	  })
	} ?: run {
	  TODO()
	}
  }

  inline fun <reified RD: MajorTypeReader<*>, R> readEachManually(op: RD.()->R) = range.map {
	lendStream(CborItemReader(), andIndent = true) {
	  readManually<RD, R> { op() }
	}
  }

}