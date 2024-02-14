package fi.juka.library

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * The Accelerometer class represents an accelerometer sensor and provides methods
 * to register and unregister listeners, as well as filter accelerometer data.
 */
class Accelerometer(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as
            SensorManager
    private var accelerometer: Sensor? = null
    private var acceleration: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var gravity: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var listener: AccelerometerListener? = null

    /**
     * Initializes the accelerometer sensor.
     */
    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    override fun onSensorChanged(event: SensorEvent?) {

        // If true, clone it to the array and send it to the accelerometer listener
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            acceleration = event.values.clone()
            listener?.onAccelerationChanged(acceleration)
        }
    }

    /**
     * Registers an AccelerometerListener to receive accelerometer events.
     *
     * @param listener the listener to be registered
     */
    fun register(listener: AccelerometerListener) {
        this.listener = listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    /**
     * Unregisters the currently registered AccelerometerListener.
     */
    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Filters accelerometer data to remove gravitational noise and isolate the device's true motion.
     *
     * @param acceleration the raw accelerometer data to be filtered
     * @param alpha the smoothing factor for the low-pass filter, defaults to 0.95
     * @return the filtered accelerometer data
     */
    fun filter(acceleration: FloatArray, alpha: Float = 0.95f): FloatArray {

        gravity[0] = alpha * gravity[0] + (1 - alpha) * acceleration[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * acceleration[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * acceleration[2]

        acceleration[0] = acceleration[0] - gravity[0]
        acceleration[1] = acceleration[1] - gravity[1]
        acceleration[2] = acceleration[2] - gravity[2]

        return acceleration
    }
}

