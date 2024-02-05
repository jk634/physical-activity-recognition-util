package fi.juka.library

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AccelerationDataBufferTest {

    private lateinit var dataBuffer: AccelerationDataBuffer

    @Before
    fun setUp() {
        // Initialize buffer size as 3
        dataBuffer = AccelerationDataBuffer(bufferSize = 3)
    }

    @Test
    fun testAddData() {
        dataBuffer.addData(Triple(1.0, 2.0, 3.0), 1000L)
        assertEquals(1, dataBuffer.getData().size)
    }

    @Test
    fun testGetDataEmptyBuffer() {
        assertEquals(0, dataBuffer.getData().size)
    }

    @Test
    fun testGetDataNonEmptyBuffer() {
        dataBuffer.addData(Triple(1.0, 2.0, 3.0), 1000L)
        dataBuffer.addData(Triple(4.0, 5.0, 6.0), 2000L)
        assertEquals(2, dataBuffer.getData().size)
    }

    @Test
    fun testAddDataWithBufferLimit() {
        dataBuffer.addData(Triple(1.0, 2.0, 3.0), 1000L)
        dataBuffer.addData(Triple(4.0, 5.0, 6.0), 2000L)
        dataBuffer.addData(Triple(7.0, 8.0, 9.0), 3000L)
        dataBuffer.addData(Triple(10.0, 11.0, 12.0), 4000L)

        assertEquals(3, dataBuffer.getData().size)
    }

    @Test
    fun testAddDataOrder() {
        dataBuffer.addData(Triple(1.0, 2.0, 3.0), 1000L)
        dataBuffer.addData(Triple(4.0, 5.0, 6.0), 2000L)
        dataBuffer.addData(Triple(7.0, 8.0, 9.0), 3000L)

        val data = dataBuffer.getData()

        assertEquals(Triple(1.0, 2.0, 3.0), data[0].first)
        assertEquals(Triple(4.0, 5.0, 6.0), data[1].first)
        assertEquals(Triple(7.0, 8.0, 9.0), data[2].first)
    }

    @Test
    fun testEmptyData() {
        dataBuffer.addData(Triple(1.0, 2.0, 3.0), 1000L)
        dataBuffer.emptyData()
        assertEquals(0, dataBuffer.getData().size)
    }
}