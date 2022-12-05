package matt.cbor.data.major.tag

import matt.cbor.data.major.CborDataItem

class CborTag<T>(val tagValue: ULong, val content: CborDataItem<T>): CborDataItem<T> {
  override fun info() = "tag: $tagValue, content: $content"
  override val raw by lazy { content.raw }
  override val isBreak get() = false
}