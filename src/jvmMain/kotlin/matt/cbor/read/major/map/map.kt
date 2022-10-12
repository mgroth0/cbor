package matt.cbor.read.major.map

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.map.CborMap
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.major.IntArgTypeReader
import matt.cbor.read.major.MajorTypeReader

class MapReader(head: HeadWithArgument): IntArgTypeReader<CborMap<*, *>>(head) {
  override fun readImpl(): CborMap<Any?, Any?> {
	return argumentValue?.let {
	  val data = (range).associate { next() }
	  CborMap(data)
	} ?: run {
	  TODO()
	}
  }

  fun next() = lendStream(CborItemReader()) {
	read() to run {
	  read()
	}
  }

  inline fun <reified T> nextKeyOnly() = lendStream(CborItemReader()) {
	read().raw as T
  }


  inline fun <reified T> nextValue(requireKeyIs: Any): T {
	val n = next()
	require(n.first.raw == requireKeyIs)
	return n.second.raw as T
  }


  inline fun <reified RD: MajorTypeReader<*>, R> nextValueManual(
	requireKeyIs: Any,
	op: RD.()->R
  ) = lendStream(CborItemReader()) {
	val k = read()
	require(k.raw == requireKeyIs) {
	  "expected key \"${requireKeyIs}\" but got key ${k.raw}"
	}
	readManually<RD, R> { op() }
  }

  inline fun <reified RD: MajorTypeReader<*>, R> nextValueManualDontReadKey(
	op: RD.()->R
  ) = lendStream(CborItemReader()) {
	readManually<RD, R> { op() }
  }


  private fun nextAndStoreBytes() = lendStream(CborItemReader()) {
	readAndStoreBytes() to run {
	  readAndStoreBytes()
	}
  }

  override fun readAndStoreBytes(): CborReadResultWithBytes<CborMap<*, *>> {

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
	}

  }

}