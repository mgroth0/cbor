@file:OptIn(ExperimentalSerializationApi::class)

package matt.cbor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import matt.file.JioFile
import matt.file.ext.mkparents
import matt.model.obj.text.WritableBytes
import matt.model.ser.ExternalSerializersModule
import java.io.OutputStream


inline fun <reified T> T.toCborEncodedBytes(cbor: Cbor = MyCbor) = cbor.encodeToByteArray(this)
inline fun <reified T> OutputStream.writeAsCbor(any: T) = write(any.toCborEncodedBytes())


inline fun <reified T> JioFile.loadCbor(): T {
//    TestCommonThreadObject
//    TestCommonJvmAndroidThreadObject
//    TestAndroidThreadObject
//    TestJvmThreadObject
    try {
        return readBytes().loadCbor()
    } catch (e: SerializationException) {
        println("SERIALIZATION ERROR WHEN LOADING FILE $this")
        throw e
    }
}


inline fun <reified T> ByteArray.loadCbor(
    cbor: Cbor = MyCbor,
): T = cbor.decodeFromByteArray(this)


inline fun <reified T> JioFile.loadOrSaveCbor(
    forceRecreate: Boolean = false,
    cbor: Cbor = MyCbor,
    op: () -> T
): T {
    return if (!forceRecreate && exists()) {
        loadCbor()
    } else op().also {
        mkparents()
        writeBytes(cbor.encodeToByteArray(it))
    }
}


inline fun <reified T> WritableBytes.saveCbor(
    t: T,
    cbor: Cbor = MyCbor,
) {
    bytes = (cbor.encodeToByteArray(t))
}


inline fun <reified T : Any> T.saveAsCborTo(f: WritableBytes) = f.saveCbor(this)

val MyCbor by lazy {
    Cbor {
        serializersModule = ExternalSerializersModule
    }
}