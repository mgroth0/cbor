package matt.cbor.data.major.map

import matt.cbor.data.major.CborDataItem
import matt.lang.tostring.SimpleStringableClass

class CborMap<K, V>(val value: Map<out CborDataItem<out K>, CborDataItem<out V>>) :
    SimpleStringableClass(),
    CborDataItem<Map<K, V>> {
    override val raw: Map<K, V> by lazy {
        value.entries.associate { it.key.raw to it.value.raw }
    }

    override fun info() = value.toString()
    override val isBreak get() = false

    override fun toStringProps() =
        mapOf(
            "size" to raw.size
        ) +
            run {
                if (raw.size <= 10) {
                    mapOf(
                        "Some Entries (need a better way to put hierarchical data in SimpleStringableClass strings)" to
                            raw.map { (k, v) ->
                                "\n\t$k\t$v"
                            }.joinToString("")
                    )
                } else mapOf()
            }
}
