package matt.cbor.read.major.nint

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.nint.CborNegInt
import matt.cbor.read.major.IntArgTypeReader

class NegIntReader(head: HeadWithArgument): IntArgTypeReader<CborNegInt>(head) {
  override fun read(): CborNegInt {
	return CborNegInt(argumentValue = count)
  }
}