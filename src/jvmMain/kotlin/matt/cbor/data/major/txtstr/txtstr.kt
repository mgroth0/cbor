package matt.cbor.data.major.txtstr

import matt.cbor.data.major.CborDataItem

class CborTextString(val value: String): CborDataItem {
  override fun info() = value
}