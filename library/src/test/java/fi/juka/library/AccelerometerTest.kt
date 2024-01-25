package fi.juka.library

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class AccelerometerTest {

    private lateinit var context: Context
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var listener: AccelerometerListener
    private lateinit var accelerometerInstance: Accelerometer

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        sensorManager = mock(SensorManager::class.java)
        accelerometer = mock(Sensor::class.java)
        listener = mock(AccelerometerListener::class.java)

        `when`(context.getSystemService(Context.SENSOR_SERVICE)).thenReturn(sensorManager)
        `when`(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(accelerometer)

        accelerometerInstance = Accelerometer(context)
    }

    @Test
    fun testRegisterListener() {
        accelerometerInstance.register(listener)
        verify(sensorManager).registerListener(eq(accelerometerInstance), eq(accelerometer), anyInt())
    }

    @Test
    fun testUnregisterListener() {
        accelerometerInstance.unregister()
        verify(sensorManager).unregisterListener(accelerometerInstance)
    }

    @Test
    fun testFilterAcceleration() {
        val acceleration = floatArrayOf(1f, 2f, 3f)
        val filteredAcceleration = accelerometerInstance.filter(acceleration)

        assertEquals(3, filteredAcceleration.size)
    }
}