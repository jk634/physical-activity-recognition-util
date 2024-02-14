# Activity Recognition Library
This library provides Android developers with tools to recognize different physical activities using the accelerometer sensor data. 

The library includes features such as:

- __Activity Recognition:__ Recognizes user's physical activities in real-time.
- __Utilization of Accelerometer:__ Utilize the smartphone’s accelerometer for accurate activity detection.
- __Flexible Integration:__ Easily integrate into Android projects.
- __Clear Documentation:__ Provides comprehensive documentation.

## Documentation

Documentation is available at https://jk634.github.io/physical-activity-recognition-util/ .

## Installation

Make sure to include maven `url 'https://jitpack.io'` in your root build.gradle file, to ensure the library can be fetched from JitPack repository:

```kotlin
allprojects {
    repositories {
		...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependency to your module-level build.gradle file to integrate the library into your project:

```kotlin
implementation 'com.github.jk634:juka:physical-activity-recognition-util:1.1.0'
```

## Usage Examples
### Accelerometer
To utilize the accelerometer and gather activity data from it, you need to create an activity that implements the AccelerometerListener interface. Here's an example:

```kotlin
class MainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var acceleration: FloatArray
    private lateinit var accelerometer: Accelerometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the accelerometer and register the listener
        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        // Handle new accelerometer data here as needed
        this.acceleration = acceleration
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        // Filtered acceleration data
        this.acceleration = accelerometer.filter(acceleration)
    }

    override fun onResume() {
        super.onResume()
        // Re-register the listener when the activity comes to the foreground
        accelerometer.register(this)
    }

    override fun onPause() {
        super.onPause()
        // Unregister when the activity goes to the background
        accelerometer.unregister()
    }
}
```
Here, the AccelerometerListener interface is implemented, allowing the reception of accelerometer data. In the onCreate() method, the accelerometer is initialized and the listener is registered. The onResume() and onPause() methods ensure that the listener is properly registered and unregistered during the activity's lifecycle, allowing you to handle new accelerometer data as needed. 

I recommend you to filter acceleration data using the filter method because it helps remove the effects of gravity from the raw accelerometer readings. By applying a low-pass filter with a smoothing factor (alpha), the filter function separates gravitational acceleration from the overall acceleration. You have the option to specify your own alpha value along with the accelerometer data. The default value is set to 0.95f.

That's all for registering the accelerometer and getting data from it!

### Database

It's up to you how you would like to save your acceleration data. The only requirement is that you must use the TrainingData class for successful data comparison. Here's how the TrainingData class looks like:
```kotlin
class TrainingData(
    val id: Long,
    val x_axis: Float,
    val y_axis: Float,
    val z_axis: Float,
    val total_acceleration: Float,
    val timestamp: Long,
    val activityId: Long
) {
}
```
Where
- __id__ represents a unique identifier for each acceleration data
- __x_axis__, __y_axis__, and __z_axis__ indicate the acceleration values along the respective axes
- __total_acceleration__ denotes the overall acceleration value, which you can calculate using the formula: sqrt(x_axis^2 + y_axis^2 + z_axis^2)
- __timestamp__ signifies the time when the acceleration data is recorded
- __activityId__ associates with the activity represented by the acceleration data

I have a database package ready for use, and you can utilize it along with its methods as per your preference. You can find the database documentation [here](https://jk634.github.io/physical-activity-recognition-util/library/fi.juka.library.database/index.html).

### Data comparision

DataCompare class preprocesses the activity data before comparison. Then, it compares real-time average accelerations to preprocessed training data to identify the user's performed activity. 
For example: 

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // example data
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2F, 0.5F, 0.6F, 0.0F, 0, 1),
                TrainingData(2, 1.4F, 0.6F, 0.5F,0.0F, 0,  1)
            ),
            2L to listOf(
                TrainingData(3, 3.5F, 2.1F, 2.4F,0.0F, 0,2),
                TrainingData(4, 4.0F, 2.3F, 2.4F,0.0F, 0, 2)
            ),
            3L to listOf(
                TrainingData(5, 2.0F, 1.8F, 2.5F,0.0F, 0,3),
                TrainingData(6, 1.5F, 1.6F, 2.0F,0.0F, 0,3)
            )
        )

        // Context initialization
        val dataCompare = DataCompare(this)

        // Preprocessing the activity data
        dataCompare.preprocessing(
            listOf(
                Pair(1L, "Walking"),
                Pair(2L, "Cycling"),
                Pair(3L, "Running")
            ),
            trainingData
        )

        // Test real-time total average acceleration
        val realTimeTotAvrgAcc = 1.47

        // Comparing real-time total average acceleration with preprocessed activity data
        dataCompare.compareDataAverages(realTimeTotAvrgAcc) { recognizedActivity ->
            Log.d("Activity test", "Recognized Activity: $recognizedActivity")
        }
    }
}
```
Here, example data is created from training data for different activities such as walking, cycling, and running. This training data is passed to the DataCompare class.

> [!NOTE]  
> Data comparer currently doesn't require total acceleration or timestamp, so you can set them as 0.0F and 0 for now.

> [!IMPORTANT]
> You need to use the preprocessing function on the list of activities and the training data list before you can use the compareDataAverages function to obtain the threshold and the average total accelerations of the activities.

As you can see from the example, in data comparison, it is necessary to compare the average total accelerations of real-time acceleration values with previously stored acceleration data to use the comparison. You can use the AccelerationDataBuffer class to assist with this. You can either use it by buffering data with a predetermined buffer size:

```kotlin
AccelerationDataBuffer(bufferSize = 40)
```
Or use it for temporarily storing and removing data, as shown in the following example:
```kotlin
override fun onAccelerationChanged(acceleration: FloatArray) {
    var accData = accelerometer.filter(acceleration, 0.8f)

    accelerationBuffer.addData(
        Triple(accData[0].toDouble(), accData[1].toDouble(), accData[2].toDouble()),
        System.currentTimeMillis()
    )
        
    val currentData = accelerationBuffer.getData()
    
    if (currentData.size == 40) {
        val x = currentData.map { it.first.first }
        val y = currentData.map { it.first.second }
        val z = currentData.map { it.first.third }
        val time = currentData.map { it.second }

        val averageTotalAcceleration = 
        AccelerationUtils.calculateAverageTotalAcceleration(x,y,z)

        comparision.compareDataAverages(averageTotalAcceleration) { activityName ->
            if (activityName != null && activityName.isNotEmpty()) {
                // Do something with "$activityName"
            }
        }
        accelerationBuffer.emptyData()
    }
}
```
Or you can employ a simplified approach, such as:
```kotlin
override fun onAccelerationChanged(acceleration: FloatArray) {
    val accData = accelerometer.filter(acceleration, 0.7f)

    currentData.add(floatArrayOf(accData[0], accData[1], accData[2]))

    if (currentData.size == 40) {
        val x = currentData.map { it[0].toDouble() }
        val y = currentData.map { it[1].toDouble() }
        val z = currentData.map { it[2].toDouble() }

        val avrgTotalAcc = AccelerationUtils.calculateAverageTotalAcceleration(x,y,z)

        comparison.compareDataAverages(avrgTotalAcc) { activityName ->
            val formattedSpeed = "%.3f".format(avrgTotalAcc)
            currentActivity!!.text = "$activityName\n\ntotal acceleration\n$formattedSpeed"
        }
        currentData.clear()
    }
}
```