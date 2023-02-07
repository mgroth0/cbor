package matt.cbor.data.major.seven

import matt.cbor.data.major.CborDataItem
import matt.lang.Undefined
import matt.reflect.tostring.toStringBuilder

sealed interface CborFloatOrSimpleValue<T>: CborDataItem<T>

abstract class CborBoolean(override val raw: Boolean): CborFloatOrSimpleValue<Boolean> {
  override fun info() = "bool: $raw"
  override val isBreak = false
}

object CborFalse: CborBoolean(false)
object CborTrue: CborBoolean(true)


object CborUndefined: CborFloatOrSimpleValue<Undefined> {
  override fun info() = "special: undefined"
  override val raw = Undefined
  override val isBreak = false
}

object CborNull: CborFloatOrSimpleValue<Any?> {
  override val raw = null
  override fun info() = "special: null"
  override fun toString() = "CborNull"
  override val isBreak = false
}

class CborFloat(override val raw: Float): CborFloatOrSimpleValue<Float> {
  override fun info() = "float: $raw"
  override fun toString() = toStringBuilder(::raw)
  override val isBreak get() = false
  //  override fun infoString(): String {
  //	return "float: $raw"
  //  }
}

class CborDouble(override val raw: Double): CborFloatOrSimpleValue<Double> {
  override fun info() = "double: $raw"
  override fun toString() = toStringBuilder(::raw)
  override val isBreak get() = false
  //  override fun infoString(): String {
  //	return "double: $raw"
  //  }
}


/*"not a data item"*/
object CborBreak: CborFloatOrSimpleValue<Unit> {
  const val argumentCode = 31
  const val argumentCodeByte = argumentCode.toByte()
  val int = 255.toUInt()
  val uByte = int.toUByte()
  val byte = uByte.toByte()
  override val raw = Unit
  override fun info() = "Cbor break is not a data item!"
  override fun toString() = "CborBreak"
  override val isBreak = true
  //  override fun infoString(): String {
  //	return "BREAK"
  //  }
}