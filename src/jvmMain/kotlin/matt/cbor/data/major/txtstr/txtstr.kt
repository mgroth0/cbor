package matt.cbor.data.major.txtstr

import matt.cbor.data.major.CborDataItem
import matt.model.obj.tostringbuilder.toStringBuilder

class CborTextString(override val raw: String): CborDataItem<String> {
  override fun info() = "\"$raw\""
  override fun toString() = toStringBuilder(::raw)
  override val isBreak get() = false
}