@file:JvmName("CborJvmAndroidKt")

package matt.cbor

import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import matt.file.JioFile
import matt.file.ext.mkparents
import java.io.OutputStream


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


inline fun <reified T> JioFile.loadOrSaveCbor(
    forceRecreate: Boolean = false,
    cbor: Cbor = MyCbor,
    op: () -> T
): T = if (!forceRecreate && exists()) {
    loadCbor()
} else op().also {
    mkparents()
    writeBytes(cbor.encodeToByteArray(it))
}





