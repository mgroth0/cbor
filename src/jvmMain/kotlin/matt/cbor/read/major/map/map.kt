package matt.cbor.read.major.map

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.map.CborMap
import matt.cbor.read.major.IntArgTypeReader

class MapReader(head: HeadWithArgument): IntArgTypeReader<CborMap>(head) {
  override fun read(): CborMap {
	return argumentValue?.let { len ->
	  CborMap((0.toULong()..len).associate {
		lendStream(CborItemReader()) {
		  read() to read()
		}
	  })
	} ?: run {
	  TODO()
	}
  }
}