package matt.cbor.err

import matt.cbor.data.major.MajorType
import kotlin.reflect.KClass

@PublishedApi
internal val PARSER_BUG: Nothing get() = throw RuntimeException("Problem on the cbor parser end")

@PublishedApi
internal val NOT_WELL_FORMED: Nothing get() = throw NotWellFormedException()

abstract class CborParseException internal constructor(message: String? = null): Exception(message)
class NotWellFormedException: CborParseException()
abstract class UnexpectedFormatException(message: String): CborParseException(message)
class UnexpectedMajorTypeException(
  expected: KClass<*>,
  received: MajorType
): UnexpectedFormatException("expected $expected but received $received")

class UnexpectedCountException(
  expected: ULong,
  received: ULong
): UnexpectedFormatException("expected count $expected but received $received")