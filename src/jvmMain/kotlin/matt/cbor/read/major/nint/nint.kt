package matt.cbor.read.major.nint

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.nint.CborNegInt
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.major.IntArgTypeReader

class NegIntReader(head: HeadWithArgument): IntArgTypeReader<CborNegInt>(head) {
  override fun readImpl(): CborNegInt {
	return CborNegInt(argumentValue = count)
  }
  override fun readAndStoreBytes(): CborReadResultWithBytes<CborNegInt> {
	return CborReadResultWithBytes(readImpl(), byteArrayOf())
  }
}