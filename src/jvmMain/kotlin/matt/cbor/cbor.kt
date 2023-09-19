@file:OptIn(ExperimentalSerializationApi::class)

package matt.cbor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import matt.file.JioFile
import matt.file.ext.mkparents
import matt.model.code.yes.YesIUse
import matt.model.obj.text.WritableBytes
import java.io.OutputStream


inline fun <reified T> T.toCborEncodedBytes() = Cbor.encodeToByteArray(this)
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


inline fun <reified T> ByteArray.loadCbor(): T = Cbor.decodeFromByteArray(this)


object YesIUseCbor : YesIUse

inline fun <reified T> JioFile.loadOrSaveCbor(
    forceRecreate: Boolean = false,
    op: () -> T
): T {
    return if (!forceRecreate && exists()) {
        loadCbor()
    } else op().also {
        mkparents()
        writeBytes(Cbor.encodeToByteArray(it))
    }
}


inline fun <reified T> WritableBytes.saveCbor(t: T) {
    bytes = (Cbor.encodeToByteArray(t))
}


inline fun <reified T : Any> T.saveAsCborTo(f: WritableBytes) = f.saveCbor(this)