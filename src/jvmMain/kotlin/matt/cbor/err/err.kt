package matt.cbor.err

import matt.cbor.data.major.MajorType
import kotlin.reflect.KClass


abstract class CborParseException internal constructor(message: String? = null): Exception(message)

class ParserBugException(message: String): CborParseException(message)

@PublishedApi internal fun parserBug(message: String): Nothing = throw ParserBugException(message)


class NotWellFormedException: CborParseException()

@PublishedApi internal val NOT_WELL_FORMED: Nothing get() = throw NotWellFormedException()


abstract class UnexpectedFormatException(message: String): CborParseException(message)
class UnexpectedMajorTypeException(
  expected: KClass<*>,
  received: MajorType
): UnexpectedFormatException("expected $expected but received $received")

class UnexpectedCountException(
  expected: ULong,
  received: ULong
): UnexpectedFormatException("expected count $expected but received $received")

class UnexpectedCountNotInRangeException(
  expected: ULongRange,
  received: ULong
): UnexpectedFormatException("expected count $expected but received $received")
