package matt.cbor.read.major.map

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.map.CborMap
import matt.cbor.read.major.IntArgTypeReader
import matt.cbor.read.major.MajorTypeReader

class MapReader(head: HeadWithArgument): IntArgTypeReader<CborMap>(head) {
  override fun readImpl(): CborMap {
	return argumentValue?.let {
	  CborMap((range).associate { next() })
	} ?: run {
	  TODO()
	}
  }

  fun next() = lendStream(CborItemReader(), andIndent = true) {
	read() to read()
  }

  inline fun <reified T> nextValue(requireKeyIs: Any): T {
	val n = next()
	require(n.first.raw == requireKeyIs)
	return n.second.raw as T
  }

  inline fun <reified RD: MajorTypeReader<*>> nextValueManual(
	requireKeyIs: Any,
	op: RD.()->Unit
  ) = lendStream(CborItemReader(), andIndent = true) {
	val k = read()
	require(k.raw == requireKeyIs)
	val v = readManually<RD> { op() }
  }
}