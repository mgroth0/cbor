package matt.cbor.data.major.array

import matt.cbor.data.major.CborDataItem

class CborArray<T>(val items: List<CborDataItem<T>>): CborDataItem<List<T>> {
  override val raw by lazy {
	items.map { it.raw }
  }

  override fun info() = items.toString()
}