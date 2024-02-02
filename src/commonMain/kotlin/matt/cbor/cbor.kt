
package matt.cbor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import matt.model.obj.text.WritableBytes
import matt.model.op.convert.BytesConverter
import matt.model.ser.ExternalSerializersModule


@OptIn(ExperimentalSerializationApi::class)
class CborBytesConverter<T>(private val ser: KSerializer<T>) : BytesConverter<T> {
    override fun toBytes(t: T): ByteArray {
//        TestCommonThreadObject
//        TestCommonJvmAndroidThreadObject
//        TestAndroidThreadObject
//        TestJvmThreadObject
        return Cbor.encodeToByteArray(
            ser,
            t
        )
    }

    override fun fromBytes(s: ByteArray): T = Cbor.decodeFromByteArray(
        ser,
        s
    )

}





inline fun <reified T> ByteArray.loadCbor(
    cbor: Cbor = MyCbor,
): T = cbor.decodeFromByteArray(this)




val MyCbor by lazy {
    Cbor {
        serializersModule = ExternalSerializersModule
    }
}

inline fun <reified T> T.toCborEncodedBytes(cbor: Cbor = MyCbor) = cbor.encodeToByteArray(this)

inline fun <reified T> WritableBytes.saveCbor(
    t: T,
    cbor: Cbor = MyCbor,
) {
    bytes = (cbor.encodeToByteArray(t))
}

inline fun <reified T : Any> T.saveAsCborTo(f: WritableBytes) = f.saveCbor(this)
