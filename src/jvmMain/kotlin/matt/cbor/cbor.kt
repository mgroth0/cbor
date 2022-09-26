package matt.cbor

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.head.InitialByte
import matt.cbor.data.major.CborDataItem
import matt.cbor.log.INDENT
import matt.cbor.read.CborReaderTyped
import matt.cbor.read.head.HeadReader
import matt.cbor.read.major.MajorTypeReader
import matt.prim.str.times
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract


class CborItemReader: CborReaderTyped<CborDataItem<*>>() {

  /*the payload itself already prints log info in its own read()*/
  override fun read() = readImpl()

  override fun readImpl(): CborDataItem<*> = readManually<MajorTypeReader<*>, CborDataItem<*>> {
	read()
  }


  fun readHeader(): HeadWithArgument {
	val initialByte = InitialByte(readByte())
	val headReader = HeadReader(initialByte)
	val head = lendStream(headReader) { read() }
	logger?.plusAssign(INDENT*indent + head.info())
	return head
  }


  inline fun <reified RD: MajorTypeReader<*>, R> readManually(op: RD.()->R): R {
	contract {
	  callsInPlace(op, EXACTLY_ONCE)
	}
	val head = readHeader()
	val payloadReader = head.majorType.reader(head) as RD
	return lendStream(payloadReader) {
	  setIndentOf(this)
	  op()
	}
  }


}

