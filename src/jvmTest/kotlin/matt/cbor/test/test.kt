package matt.cbor.test


import matt.cbor.data.major.array.CborArray
import matt.cbor.data.major.seven.CborDouble
import matt.cbor.data.major.seven.CborFalse
import matt.cbor.data.major.seven.CborFloat
import matt.cbor.data.major.seven.CborNull
import matt.cbor.data.major.seven.CborTrue
import matt.cbor.data.major.seven.CborUndefined
import matt.cbor.data.major.tag.CborTag
import matt.cbor.loadCbor
import matt.lang.model.file.MacFileSystem
import matt.lang.assertions.require.requireEquals
import matt.test.assertions.JupiterTestAssertions.assertRunsInOneMinute
import kotlin.test.Test

class CborTests {
    @Test
    fun testReadUnsignedInt() = assertRunsInOneMinute {
        val int = 5
        val byte = int.toByte()
        val loadedInt = byteArrayOf(byte).loadCbor<Int>()
        requireEquals(
            loadedInt, int
        )

    }

    @Test
    fun readCborFile() = assertRunsInOneMinute {
        val int = 5
        val byte = int.toByte()
        val tempFile = with(MacFileSystem) {
            matt.file.ext.createTempFile()
        }
        tempFile.bytes = byteArrayOf(byte)
        requireEquals(
            tempFile.loadCbor<Int>(), int
        )
    }

    @Test
    fun initObjects() = assertRunsInOneMinute {
        CborFalse
        CborTrue
        CborUndefined
        CborNull
    }

    @Test
    fun instantiateClasses() = assertRunsInOneMinute {
        CborArray(listOf(CborFloat(1f)))
        CborTag(ULong.MIN_VALUE, CborDouble(1.0))
    }
}