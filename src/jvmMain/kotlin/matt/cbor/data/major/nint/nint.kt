package matt.cbor.data.major.nint

import matt.cbor.data.major.CborDataItem

/*I don't think kotlin has built in classes for a negative Long that goes up to 2^64*/
/*so keep the value private for now*/
/*probably will never use this anyway*/
class CborNegInt(private val argumentValue: ULong): CborDataItem {
  private val realValue by lazy { "1 - $argumentValue" }
}