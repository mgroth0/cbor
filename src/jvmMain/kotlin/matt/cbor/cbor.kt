@file:OptIn(ExperimentalSerializationApi::class)

package matt.cbor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import matt.file.MFile
import java.io.OutputStream


fun Any.toCborEncodedBytes() = Cbor.encodeToByteArray(this)
fun OutputStream.writeAsCbor(any: Any) = write(any.toCborEncodedBytes())

inline fun <reified T> MFile.loadCbor() = Cbor.decodeFromByteArray<T>(readBytes())



