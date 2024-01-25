package fi.juka.library

/**
 * The AccelerationDataBuffer class represents a buffer for storing acceleration data
 * with associated timestamps.
 *
 * The buffer has a specified size, and when new data is added, older data may be removed
 * to maintain the buffer size.
 *
 * @param bufferSize The maximum size of the data buffer.
 */
class AccelerationDataBuffer(private val bufferSize: Int) {

    /**
     * The internal data buffer to store acceleration data along with timestamps.
     */
    private val dataBuffer = ArrayList<Pair<Triple<Double, Double, Double>, Long>>()

    /**
     * Adds new acceleration data with the associated timestamp to the buffer.
     *
     * If the buffer size is reached, the oldest data point is removed.
     *
     * @param acceleration The acceleration data as a Triple of (x, y, z) values.
     * @param time The timestamp associated with the acceleration data.
     */
    fun addData(acceleration: Triple<Double, Double, Double>, time: Long) {
        if (dataBuffer.size >= bufferSize) {
            dataBuffer.removeAt(0)
        }
        dataBuffer.add(Pair(acceleration, time))
    }

    /**
     * Retrieves the current contents of the data buffer.
     *
     * @return A List of Pairs containing acceleration data and timestamps.
     */
    fun getData(): List<Pair<Triple<Double, Double, Double>, Long>> {
        return dataBuffer.toList()
    }

    /**
     * Clears all data from the buffer.
     */
    fun emptyData() {
        dataBuffer.clear()
    }
}