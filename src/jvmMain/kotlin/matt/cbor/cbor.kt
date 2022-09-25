package matt.cbor

import matt.cbor.data.head.InitialByte
import matt.cbor.data.major.CborDataItem
import matt.cbor.log.INDENT
import matt.cbor.read.CborReaderTyped
import matt.cbor.read.head.HeadReader
import matt.prim.str.times


class CborItemReader: CborReaderTyped<CborDataItem>() {
  override fun read(): CborDataItem {
	val initialByte = InitialByte(readByte())
	val headReader = HeadReader(initialByte)
	val head = lendStream(headReader) { read() }
	logger?.plusAssign(INDENT*indent + head.info())
	return lendStream(head.majorType.reader(head)) {
	  indent = this@CborItemReader.indent + 1
	  read().also {
		logger?.plusAssign(INDENT*indent + it.info())
	  }
	}
  }
}