package matt.cbor.data.major.uint

import matt.cbor.data.major.CborDataItem

class CborUInt(val value: ULong): CborDataItem {
  override fun info() = value.toString()
}