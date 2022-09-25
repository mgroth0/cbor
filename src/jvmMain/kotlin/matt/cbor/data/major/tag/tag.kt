package matt.cbor.data.major.tag

import matt.cbor.data.major.CborDataItem

class CborTag(val tagValue: ULong, val content: CborDataItem): CborDataItem