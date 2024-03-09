package matt.cbor.data.major.seven

import matt.cbor.data.major.CborDataItem
import matt.lang.common.Undefined
import matt.reflect.tostring.PropReflectingStringableClass

sealed interface CborFloatOrSimpleValue<T> : CborDataItem<T>

abstract class CborBoolean(final override val raw: Boolean) : CborFloatOrSimpleValue<Boolean> {
    final override fun info() = "bool: $raw"
    final override val isBreak = false
}

object CborFalse : CborBoolean(false)
object CborTrue : CborBoolean(true)


object CborUndefined : CborFloatOrSimpleValue<Undefined> {
    override fun info() = "special: undefined"
    override val raw = Undefined
    override val isBreak = false
}

object CborNull : CborFloatOrSimpleValue<Any?> {
    override val raw = null
    override fun info() = "special: null"
    override fun toString() = "CborNull"
    override val isBreak = false
}

class CborFloat(override val raw: Float) : PropReflectingStringableClass(), CborFloatOrSimpleValue<Float> {
    override fun info() = "float: $raw"
    override fun reflectingToStringProps() = setOf(::raw)
    override val isBreak get() = false
}

class CborDouble(override val raw: Double) : PropReflectingStringableClass(), CborFloatOrSimpleValue<Double> {
    override fun info() = "double: $raw"
    override fun reflectingToStringProps() = setOf(::raw)
    override val isBreak get() = false
}


/*"not a data item"*/
object CborBreak : CborFloatOrSimpleValue<Unit> {
    const val argumentCode = 31
    const val argumentCodeByte = argumentCode.toByte()
    val int = 255.toUInt()
    val uByte = int.toUByte()
    val byte = uByte.toByte()
    override val raw = Unit
    override fun info() = "Cbor break is not a data item!"
    override fun toString() = "CborBreak"
    override val isBreak = true
}
