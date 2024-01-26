package fi.juka.library

import android.content.Context
import fi.juka.library.database.TrainingData

/**
 * Class for comparing activity data based on total acceleration averages.
 * It preprocesses the activity data and compares real-time total average acceleration with preprocessed data.
 *
 * @property context The context needed for initialization.
 * @property activityAverageTotalAccList A list to store activity, its ID, and its average total acceleration.
 * @property threshold The threshold used for activity comparison based on acceleration averages.
 * @property stillThreshold The threshold for considering the device as 'still'.
 */
class DataComparer(private val context: Context) {

    val activityAverageTotalAccList  = mutableListOf<Triple<Long, String, Double>>()
    private var threshold = 0.0
    private val stillThreshold = 0.1

    /**
     * Preprocesses the activity data and calculates average total acceleration for each activity.
     *
     * @param activityData List of activity data pairs containing activity ID and name.
     * @param trainingData Map containing training data for each activity ID.
     */
    fun preprocessing(activityData: List<Pair<Long, String>>, trainingData: Map<Long, List<TrainingData>>) {
        for (activity in activityData) {
            val activityId = activity.first
            val activityName = activity.second

            val samples = trainingData[activityId] ?: emptyList()

            if (samples.isNotEmpty()) {
                val averageTotalAcc = AccelerationUtils.calculateAverageTotalAcceleration(
                    samples.map { it.x_axis },
                    samples.map { it.y_axis },
                    samples.map { it.z_axis })

                activityAverageTotalAccList.add(Triple(activityId, activityName, averageTotalAcc))
            }

            if (activityAverageTotalAccList.isNotEmpty()) {
                val maxAverage = activityAverageTotalAccList.maxByOrNull { it.third }!!.third
                val minAverage = activityAverageTotalAccList.minByOrNull { it.third }!!.third

                threshold = (maxAverage - minAverage) / activityAverageTotalAccList.size
            }
        }
    }

    /**
     * Compares real-time total average acceleration with preprocessed activity data.
     * It recognizes the activity based on the comparison results.
     *
     * @param realTimeTotAvrgAcc The real-time total average acceleration to be compared.
     * @param onActivityRecognized Callback function to handle recognized activity.
     */
    fun compareDataAverages(realTimeTotAvrgAcc: Double, onActivityRecognized: (String) -> Unit) {

        // if staying still
        if (realTimeTotAvrgAcc < stillThreshold) {
            onActivityRecognized("Still")
            return
        }

        val currentActivity = activityAverageTotalAccList.find { it.third - threshold <= realTimeTotAvrgAcc
                && realTimeTotAvrgAcc <= it.third + threshold }

        currentActivity?.let {
            onActivityRecognized(it.second)
        }
    }
}