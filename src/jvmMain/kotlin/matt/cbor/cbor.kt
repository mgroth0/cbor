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
    return Cbor.decodeFromByteArray(readBytes())
  } catch (e: SerializationException) {
    println("SERIALIZATION ERROR WHEN LOADING FILE ${this}")
    throw e
  }
}



