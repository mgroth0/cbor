package matt.cbor.data.major.seven

import matt.cbor.data.major.CborDataItem
import matt.lang.Undefined

sealed interface CborFloatOrSimpleValue<T>: CborDataItem<T>

abstract class CborBoolean(override val raw: Boolean): CborFloatOrSimpleValue<Boolean> {
  override fun info() = raw.toString()
}

object CborFalse: CborBoolean(false)
object CborTrue: CborBoolean(true)


object CborUndefined: CborFloatOrSimpleValue<Undefined> {
  override fun info() = "undefined"
  override val raw = Undefined
}

object CborNull: CborFloatOrSimpleValue<Any?> {
  override val raw = null
  override fun info() = "null"
}

class CborFloat(override val raw: Float): CborFloatOrSimpleValue<Float> {
  override fun info() = raw.toString()
}

class CborDouble(override val raw: Double): CborFloatOrSimpleValue<Double> {
  override fun info() = raw.toString()
}


/*"not a data item"*/
object Break {
  val int = 255.toUInt()
  val ubyte = int.toUByte()
  val byte = ubyte.toByte()
}