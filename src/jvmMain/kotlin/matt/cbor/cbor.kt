package matt.cbor

import matt.cbor.data.head.InitialByte
import matt.cbor.data.major.CborDataItem
import matt.cbor.read.CborReaderTyped
import matt.cbor.read.head.HeadReader


class CborItemReader: CborReaderTyped<CborDataItem>() {
  override fun read(): CborDataItem {
	val initialByte = InitialByte(readByte())
	val headReader = HeadReader(initialByte)
	val head = headReader.withStream { read() }
	return head.majorType.reader(head).withStream {
	  read()
	}
  }
}