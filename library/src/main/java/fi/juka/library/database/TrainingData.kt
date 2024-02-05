package fi.juka.library.database

/**
 * TrainingData represents a single data entry for training purposes,
 * including acceleration values in x, y, and z axes, total acceleration,
 * timestamp of the data, and the associated activity ID.
 *
 * @property id The unique identifier for the training data entry.
 * @property x_axis The acceleration value in the x-axis.
 * @property y_axis The acceleration value in the y-axis.
 * @property z_axis The acceleration value in the z-axis.
 * @property total_acceleration The total acceleration magnitude calculated from the three axes.
 * @property timestamp The timestamp when the training data was recorded.
 * @property activityId The ID of the associated activity for this training data entry.
 */
class TrainingData(
    val id: Long,
    val x_axis: Float,
    val y_axis: Float,
    val z_axis: Float,
    val total_acceleration: Float,
    val timestamp: Long,
    val activityId: Long) {
}