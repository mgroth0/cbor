package matt.cbor.data.major.map

import matt.cbor.data.major.CborDataItem

class CborMap<K,V>(val value: Map<CborDataItem<out K>, CborDataItem<out V>>): CborDataItem<Map<K,V>> {
  override val raw: Map<K, V> by lazy {
    value.entries.associate { it.key.raw to it.value.raw }
  }

  override fun info() = value.toString()
}