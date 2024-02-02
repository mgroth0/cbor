package matt.cbor.data.major.txtstr

import matt.cbor.data.major.CborDataItem
import matt.reflect.tostring.PropReflectingStringableClass

class CborTextString(override val raw: String) : PropReflectingStringableClass(), CborDataItem<String> {
    override fun info() = "\"$raw\""
    override fun reflectingToStringProps() = setOf(::raw)
    override val isBreak get() = false
}
