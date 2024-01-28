package matt.cbor.read.major.map

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.CborDataItem
import matt.cbor.data.major.map.CborMap
import matt.cbor.data.major.seven.CborBreak
import matt.cbor.err.UnexpectedNullException
import matt.cbor.err.UnexpectedTypeException
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.item.CborItemReader
import matt.cbor.read.item.MightBeBreak
import matt.cbor.read.item.isNotBreak
import matt.cbor.read.major.IntArgTypeReader
import matt.cbor.read.major.MajorTypeReader
import matt.lang.NOT_IMPLEMENTED
import matt.lang.assertions.require.requireEquals
import matt.reflect.tostring.PropReflectingStringableClass

class MapReader(head: HeadWithArgument) : IntArgTypeReader<CborMap<*, *>>(head) {


    override fun readImpl(): CborMap<Any?, Any?> {

        return CborMap(readAsSequence().associate { it.key to it.value })

        /*return argumentValue?.let {
          val data = (range).associate { next() }
          CborMap(data)
        } ?: run {
          val items = LinkedList<Pair<CborDataItem<*>, CborDataItem<*>>>()
          do {
            val nextReader = CborItemReader()
            val key = lendStream(nextReader) {
              read()
            }
            if (key != CborBreak) {
              val value = lendStream(nextReader) {
                read()
              }
              items += key to value
            }
          } while (key != CborBreak)
          CborMap(items.associate { it.first to it.second })
        }*/
    }

    fun next() = lendStream(CborItemReader()) {
        read() to run {
            read()
        }
    }

    inline fun <reified T> nextKeyOrValueOnly() = lendStream(CborItemReader()) {
        val r = read().raw
        if (r !is T) {
            if (r == null) {
                throw UnexpectedNullException()
            } else {
                throw UnexpectedTypeException(expected = T::class, received = r::class)
            }
        }
        r
    }

    inline fun <reified T> nextKeyOrValueOnly(requireIs: T) = lendStream(CborItemReader()) {
        val k = read().raw as T
        requireEquals(k, requireIs)
        k
    }


    inline fun <reified T> nextValue(requireKeyIs: Any): T {
        val n = next()
        val k = n.first.raw
        requireEquals(k, requireKeyIs)
        return n.second.raw as T
    }


    inline fun <reified RD : MajorTypeReader<*>, R> nextValueManual(
        requireKeyIs: Any,
        op: RD.() -> R
    ) = lendStream(CborItemReader()) {
        val k = read()
        requireEquals(k.raw, requireKeyIs) {
            "expected key \"${requireKeyIs}\" but got key ${k.raw}"
        }
        readManually<RD, R> { op() }
    }

    inline fun <reified RD : MajorTypeReader<*>, R> nextValueManualDontReadKey(
        op: RD.() -> R
    ) = lendStream(CborItemReader()) {
        readManually<RD, R> { op() }
    }


    fun nextAndStoreBytes() = lendStream(CborItemReader()) {
        readAndStoreBytes() to run {
            readAndStoreBytes()
        }
    }

    override fun readAndStoreBytes(): CborReadResultWithBytes<CborMap<Any?, Any?>> {

        //	val seq = readAsSequenceWithBytes()    //	println("In Cbor Map Reader ${hashCode()}")
        /*used to to seq.toList() but that led to a stack overflow exception!!??*/
        val itemsWithBytes = readAsSequenceWithBytes().toList()


        //	  LinkedList<Entry<CborReadResultWithBytes<CborDataItem<*>>, CborReadResultWithBytes<CborDataItem<*>>>>()
        //	for (it in seq) {
        //	  itemsWithBytes += it
        ////	  println("it=${it}")
        //	}


        var bytes = byteArrayOf()
        val data = (itemsWithBytes).associate {
            bytes += it.key.bytes
            bytes += it.value.bytes
            it.key.result to it.value.result
        }

        if (!hasCount) bytes += CborBreak.byte


        val cborMap = CborMap(data)


        //	r.appl
        return CborReadResultWithBytes(cborMap, bytes)

        /*
            return CborReadResultWithBytes(
              CborArray(itemsWithBytes.map { it.result }),
              itemsWithBytes.map { it.bytes }.reduce { acc, bytes -> acc + bytes })


            return argumentValue?.let {
              var bytes = byteArrayOf()
              val data = (range).associate {
                nextAndStoreBytes().let {
                  bytes += it.first.bytes
                  bytes += it.second.bytes
                  it.first.result to it.second.result
                }
              }
              CborReadResultWithBytes(CborMap(data), bytes)

            } ?: run {
              TODO()
            }*/

    }

    fun readAsSequence() = readAsSequenceBase({ read() }, { read() })

    fun readAsSequenceWithBytes(): Sequence<Entry<CborReadResultWithBytes<CborDataItem<*>>, CborReadResultWithBytes<CborDataItem<*>>>> =
        readAsSequenceBase({
            readAndStoreBytes()
        }, {
            readAndStoreBytes()
        })


    private inline fun <K : MightBeBreak, V> readAsSequenceBase(
        crossinline keyReadOp: CborItemReader.() -> K,
        crossinline valueReadOp: CborItemReader.() -> V
    ): Sequence<Entry<K, V>> = sequence {
        if (hasCount) range.map {
            val k = lendStream(CborItemReader()) {
                keyReadOp()
            }
            val v = lendStream(CborItemReader()) {
                valueReadOp()
            }
            yield(Entry(key = k, value = v))
        } else {
            do {

                //		val keyReader = CborItemReader()
                //		val isBreak = lendStream(keyReader) {
                //		  isBreak()
                //		}
                //		if (isBreak) break

                //		println("reading key in reader ${this@MapReader.hashCode()}")

                val key = lendStream(CborItemReader()) {
                    keyReadOp()
                }
                if (key.isNotBreak) {
                    val v = lendStream(CborItemReader()) {
                        valueReadOp()
                    }
                    yield(Entry(key = key, value = v))
                }

            } while (key.isNotBreak)
        }
    }

    inline fun <reified RD : MajorTypeReader<*>, R> readEachManually(op: RD.() -> R): List<R> {
        if (!hasCount) NOT_IMPLEMENTED
        return range.map {
            lendStream(CborItemReader()) {
                readManually<RD, R> { op() }
            }
        }
    }
}


class Entry<K, V>(
    val key: K,
    val value: V
): PropReflectingStringableClass() {
    override fun reflectingToStringProps() = setOf(::key, ::value)
}