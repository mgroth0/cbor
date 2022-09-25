package matt.cbor.read.major.tag

import matt.cbor.CborItemReader
import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.tag.CborTag
import matt.cbor.read.major.IntArgTypeReader

class TagReader(head: HeadWithArgument): IntArgTypeReader<CborTag>(head) {
  override fun read(): CborTag {
	return CborTag(tagValue = argumentValue!!, content = lendStream(CborItemReader()) { read() })
  }
}