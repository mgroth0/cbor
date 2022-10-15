package matt.cbor.read.major.array

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.array.CborArray
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.item.CborItemReader
import matt.cbor.read.major.IntArgTypeReader
import matt.cbor.read.major.MajorTypeReader

class ArrayReader(head: HeadWithArgument): IntArgTypeReader<CborArray<*>>(head) {
  override fun readImpl(): CborArray<*> {
	return argumentValue?.let {
	  CborArray(range.map {
		lendStream(CborItemReader()) {
		  read()
		}
	  })
	} ?: run {
	  TODO()
	}
  }

  inline fun <reified Raw, R> readEach(op: (Raw)->R) = range.map {
	lendStream(CborItemReader()) {
	  op(read().raw as Raw)
	}
  }

  inline fun <reified RD: MajorTypeReader<*>, R> readEachManually(op: RD.()->R) = range.map {
	lendStream(CborItemReader()) {
	  readManually<RD, R> { op() }
	}
  }


  override fun readAndStoreBytes(): CborReadResultWithBytes<CborArray<*>> {

	return argumentValue?.let {
	  val itemsAndBytes = range.map {
		lendStream(CborItemReader()) {
		  readAndStoreBytes()
		}
	  }
	  CborReadResultWithBytes(
		CborArray(itemsAndBytes.map { it.result }),
		itemsAndBytes.map { it.bytes }.reduce { acc, bytes -> acc + bytes }
	  )

	} ?: run {
	  TODO()
	}


  }


}