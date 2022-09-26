package matt.cbor.err

internal val PARSER_BUG: Nothing get() = throw RuntimeException("Problem on the cbor parser end")
internal val NOT_WELL_FORMED: Nothing get() = throw CborParseException()

class CborParseException internal constructor(): Exception()