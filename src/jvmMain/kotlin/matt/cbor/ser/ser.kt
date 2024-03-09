package matt.cbor.ser

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import matt.model.code.idea.SerIdea
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

abstract class MyCborSerializer<T: Any>(private val cls: KClass<*>): KSerializer<T>, SerIdea {

    fun canSerialize(value: Any): Boolean = value::class.isSubclassOf(cls)
    final override val descriptor: SerialDescriptor = buildClassSerialDescriptor(cls.qualifiedName!!)


  /*abstract fun deserialize(jsonElement: JsonElement): T
  abstract fun serialize(value: T): JsonElement
  fun castAndSerialize(value: Any): JsonElement*/
}
