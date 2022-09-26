package matt.cbor.data.major.txtstr

import matt.cbor.data.major.CborDataItem

class CborTextString(override val raw: String): CborDataItem<String> {
  override fun info() = "\"$raw\""
}