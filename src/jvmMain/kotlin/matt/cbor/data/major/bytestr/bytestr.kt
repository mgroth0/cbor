package matt.cbor.data.major.bytestr

import matt.cbor.data.major.CborDataItem

class CborByteString(override val raw: List<Byte>): CborDataItem<List<Byte>> {
  override fun info() = "$raw"
}