package matt.cbor.read.major.uint

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.uint.CborUInt
import matt.cbor.read.major.IntArgTypeReader

class PosOrUIntReader(head: HeadWithArgument): IntArgTypeReader<CborUInt>(head) {
  override fun readImpl(): CborUInt {
	return CborUInt(raw = count)
  }
}