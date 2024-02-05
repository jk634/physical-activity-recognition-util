package fi.juka.library

/**
 * The interface for receiving notifications about changes in accelerometer data.
 */
interface AccelerometerListener {

    /**
     * Called when the accelerometer data has changed.
     *
     * @param acceleration the new accelerometer data as a float array containing x, y, and z-axis values
     */
    fun onAccelerationChanged(acceleration: FloatArray)
}