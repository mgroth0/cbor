package matt.cbor.data.major.map

import matt.cbor.data.major.CborDataItem
import matt.reflect.tostring.toStringBuilder

class CborMap<K, V>(val value: Map<out CborDataItem<out K>, CborDataItem<out V>>): CborDataItem<Map<K, V>> {
  override val raw: Map<K, V> by lazy {
	value.entries.associate { it.key.raw to it.value.raw }
  }

  override fun info() = value.toString()
  override val isBreak get() = false

  override fun toString() = toStringBuilder(
	"size" to raw.size
  ) + run {
	if (raw.size <= 10) {
	  raw.map { (k, v) ->
		"\n\t$k\t$v"
	  }.joinToString("")
	} else ""
  }

}