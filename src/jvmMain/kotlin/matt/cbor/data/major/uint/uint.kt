package matt.cbor.data.major.uint

import matt.cbor.data.major.CborDataItem

class CborUInt(override val raw: ULong): CborDataItem<ULong> {
  override fun info() = raw.toString()
}