package matt.cbor.data.major.nint

import matt.cbor.data.major.CborDataItem
import matt.prim.int.nulong.NULong

/*I don't think kotlin has built in classes for a negative Long that goes up to 2^64*/
/*so keep the value private for now*/
/*probably will never use this anyway*/
class CborNegInt(private val argumentValue: ULong): CborDataItem<NULong> {
  override fun info() = realValue.toString()
  override val raw by lazy { NULong(argumentValue) }
  val realValue by lazy { raw.realValue }
  override val isBreak get() = false
}