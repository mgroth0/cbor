package matt.cbor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import matt.model.op.convert.BytesConverter


@OptIn(ExperimentalSerializationApi::class)
class CborBytesConverter<T>(private val ser: KSerializer<T>) : BytesConverter<T> {
    override fun toBytes(t: T): ByteArray {
        return Cbor.encodeToByteArray(
            ser,
            t
        )
    }

    override fun fromBytes(s: ByteArray): T {
        return Cbor.decodeFromByteArray(
            ser,
            s
        )
    }

}



