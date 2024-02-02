package matt.cbor.read.major.tag

import matt.cbor.data.head.HeadWithArgument
import matt.cbor.data.major.tag.CborTag
import matt.cbor.read.CborReadResultWithBytes
import matt.cbor.read.item.CborItemReader
import matt.cbor.read.major.IntArgTypeReader

class TagReader(head: HeadWithArgument) : IntArgTypeReader<CborTag<*>>(head) {
    override fun readImpl(): CborTag<*> = CborTag(tagValue = count, content = lendStream(CborItemReader()) { read() })

    override fun readAndStoreBytes(): CborReadResultWithBytes<CborTag<*>> {
        val r = lendStream(CborItemReader()) { readAndStoreBytes() }
        return CborReadResultWithBytes(
            CborTag(tagValue = count, content = r.result),
            r.bytes
        )
    }
}
