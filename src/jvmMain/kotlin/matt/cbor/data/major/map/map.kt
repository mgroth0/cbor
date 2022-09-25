package matt.cbor.data.major.map

import matt.cbor.data.major.CborDataItem

class CborMap(val value: Map<CborDataItem, CborDataItem>): CborDataItem {
  override fun info() = value.toString()
}