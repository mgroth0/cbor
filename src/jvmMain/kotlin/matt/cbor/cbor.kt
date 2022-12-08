@file:OptIn(ExperimentalSerializationApi::class)

package matt.cbor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import matt.file.MFile
import java.io.OutputStream


inline fun <reified T> T.toCborEncodedBytes() = Cbor.encodeToByteArray(this)
inline fun <reified T> OutputStream.writeAsCbor(any: T) = write(any.toCborEncodedBytes())


inline fun <reified T> MFile.loadCbor(): T {
  try {
	return readBytes().loadCbor()
  } catch (e: SerializationException) {
	println("SERIALIZATION ERROR WHEN LOADING FILE $this")
	throw e
  }
}


inline fun <reified T> ByteArray.loadCbor(): T = Cbor.decodeFromByteArray(this)


inline fun <reified T> MFile.loadOrSave(op: ()->T): T {
  return if (exists()) {
	loadCbor()
  } else op().also {
	writeBytes(Cbor.encodeToByteArray(it))
  }
}