package matt.cbor.data.major.bytestr

import matt.cbor.data.major.CborDataItem

class CborByteString(override val raw: ByteArray): CborDataItem<ByteArray> {
  override fun info() = "$raw"
  override val isBreak get() = false
}