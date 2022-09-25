package matt.cbor.data.major.seven

import matt.cbor.data.major.CborDataItem

sealed interface CborFloatOrSimpleValue: CborDataItem

abstract class CborBoolean(val value: Boolean): CborFloatOrSimpleValue {
  override fun info() = value.toString()
}

object CborFalse: CborBoolean(false)
object CborTrue: CborBoolean(true)
object CborUndefined: CborFloatOrSimpleValue {
  override fun info() = "undefined"
}

object CborNull: CborFloatOrSimpleValue {
  override fun info() = "null"
}

class CborFloat(val value: Float): CborFloatOrSimpleValue {
  override fun info() = value.toString()
}

class CborDouble(val value: Double): CborFloatOrSimpleValue {
  override fun info() = value.toString()
}


/*"not a data item"*/
object Break {
  val int = 255.toUInt()
  val ubyte = int.toUByte()
  val byte = ubyte.toByte()
}