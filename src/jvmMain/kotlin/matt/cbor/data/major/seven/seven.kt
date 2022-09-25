package matt.cbor.data.major.seven

import matt.cbor.data.major.CborDataItem

sealed interface CborFloatOrSimpleValue: CborDataItem

abstract class CborBoolean(val value: Boolean): CborFloatOrSimpleValue
object CborFalse: CborBoolean(false)
object CborTrue: CborBoolean(true)
object CborUndefined: CborFloatOrSimpleValue
object CborNull: CborFloatOrSimpleValue

class CborFloat(val value: Float): CborFloatOrSimpleValue
class CborDouble(val value: Double): CborFloatOrSimpleValue


/*"not a data item"*/
object Break {
  const val int = 255
  val byte = int.toByte()
}