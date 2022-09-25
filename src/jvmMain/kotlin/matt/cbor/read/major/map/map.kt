package matt.cbor.read.major.map

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.map.CborMap
import matt.cbor.read.major.IntArgTypeReader

class MapReader(head: HeadWithArgument): IntArgTypeReader<CborMap>(head) {
  override fun read(): CborMap {
	return argumentValue?.let {
	  CborMap((range).associate {
		lendStream(CborItemReader().also { it.indent = indent + 1 }) {
		  read() to read()
		}
	  })
	} ?: run {
	  TODO()
	}
  }
}