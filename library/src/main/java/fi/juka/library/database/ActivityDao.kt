package fi.juka.library.database

import android.content.ContentValues
import android.provider.BaseColumns
import kotlin.math.sqrt

/**
 * ActivityDao provides methods to interact with the database to manage activities and training data.
 * It encapsulates database operations related to activities and training data such as saving activities,
 * retrieving activity lists, fetching training data for activities, and deleting activities and their associated samples.
 * This class acts as a Data Access Object (DAO) for managing activities and training data.
 *
 * @param dbHelper An instance of TrainingDbHelper to access the database.
 */
class ActivityDao(private val dbHelper: TrainingDbHelper) {

    private val activityEntry = TrainingContract.ActivityEntry
    private val trainingDataEntry = TrainingContract.TrainingDataEntry

    /**
     * Saves a new activity to the database and returns its ID.
     *
     * @param activityName The name of the activity to be saved.
     * @return The ID of the newly inserted activity.
     */
    fun saveActivityAndGetId(activityName: String): Long {
        val db = dbHelper.writableDatabase
        val formattedActivityName = activityName.lowercase().replace("\\s".toRegex(), "_")

        val values = ContentValues().apply {
            put(activityEntry.COLUMN_NAME_ACTIVITY, formattedActivityName)
        }
        val id = db.insert(activityEntry.TABLE_NAME, null, values)

        db.close()

        return id
    }

    /**
     * Increments the sample count for the specified activity.
     *
     * @param activityId The ID of the activity for which the sample count should be incremented.
     */
    fun incrementSampleCount(activityId: Long) {
        val db = dbHelper.writableDatabase

        val selectQuery = "SELECT ${activityEntry.COLUMN_NAME_SAMPLES} FROM " +
                "${activityEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        var selectionArgs = arrayOf(activityId.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)
        cursor.moveToFirst()
        val oldSampleCount = cursor.getInt(cursor.getColumnIndexOrThrow("samples"))


        val values = ContentValues().apply {
            put(activityEntry.COLUMN_NAME_SAMPLES, oldSampleCount + 1)
        }

        val selection = "${BaseColumns._ID} = ?"
        selectionArgs = arrayOf(activityId.toString())

        db.update(
            activityEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        db.close()
    }

    /**
     * Retrieves a list of activities from the database.
     *
     * @return A mutable list of pairs where each pair contains the ID and name of an activity.
     */
    fun getActivitiesList(): MutableList<Pair<Long, String>> {

        val activitiesList = mutableListOf<Pair<Long, String>>()

        // Fetch all activities from the database
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM activity", null)

        // Add every activity to the list
        while (cursor.moveToNext()) {
            val activityId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            val activityName = cursor.getString(cursor.getColumnIndexOrThrow("activity_name"))
            activitiesList.add(Pair(activityId, activityName))
        }

        cursor.close()
        db.close()

        return activitiesList
    }

    /**
     * Retrieves the sample count for the specified activity.
     *
     * @param activityId The ID of the activity for which the sample count should be retrieved.
     * @return The sample count of the specified activity.
     */
    fun getSampleCount(activityId: Long): Int {
        val db = dbHelper.readableDatabase

        val selectQuery = "SELECT ${activityEntry.COLUMN_NAME_SAMPLES} FROM " +
                "${activityEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        var selectionArgs = arrayOf(activityId.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        val samples: Int = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }

        cursor.close()
        db.close()

        return samples
    }

    /**
     * Retrieves all training data associated with the specified activity.
     *
     * @param activityId The ID of the activity for which training data should be retrieved.
     * @return A mutable list of TrainingData objects associated with the specified activity.
     */
    fun getAllTrainingDataForActivity(activityId: Long): MutableList<TrainingData> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${trainingDataEntry.TABLE_NAME} WHERE " +
                "${trainingDataEntry.COLUMN_NAME_ACTIVITY_ID} = ?", arrayOf(activityId.toString()))

        val trainingDataList = mutableListOf<TrainingData>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val x_axis = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_X_AXIS))
            val y_axis = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_Y_AXIS))
            val z_axis = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_Z_AXIS))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_TIMESTAMP))
            val activityId = cursor.getLong(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_ACTIVITY_ID))
            val total_acceleration = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_TOTAL_ACCELERATION))

            val trainingData = TrainingData(id, x_axis, y_axis, z_axis, total_acceleration, timestamp, activityId)
            trainingDataList.add(trainingData)
        }

        cursor.close()
        db.close()

        return trainingDataList
    }

    /**
     * Saves accelerometer data for the specified activity to the database.
     *
     * @param accelerations A list of accelerometer data to be saved.
     * @param activityId The ID of the activity to which the accelerometer data belongs.
     */
    fun saveData(accelerations: MutableList<FloatArray>, activityId: Long) {

        lateinit var values: ContentValues
        val db = dbHelper.writableDatabase

        for (acc in accelerations) {
            val x = acc[0].toDouble()
            val y = acc[1].toDouble()
            val z = acc[2].toDouble()

            val totalAcc = sqrt(x*x + y*y + z*z)

            values = ContentValues().apply {
                put(trainingDataEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis())
                put(trainingDataEntry.COLUMN_NAME_X_AXIS,acc[0])
                put(trainingDataEntry.COLUMN_NAME_Y_AXIS,acc[1])
                put(trainingDataEntry.COLUMN_NAME_Z_AXIS, acc[2])
                put(trainingDataEntry.COLUMN_NAME_TOTAL_ACCELERATION, totalAcc)
                put(trainingDataEntry.COLUMN_NAME_ACTIVITY_ID, activityId)
            }
            if (values.size() > 0) {
                db.insert(trainingDataEntry.TABLE_NAME, null, values)
            }
        }
        db.close()
    }

    /**
     * Deletes the activity with the specified ID from the database.
     *
     * @param activityId The ID of the activity to be deleted.
     */
    fun deleteActivity(activityId: Long) {
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(activityId.toString())
        db.delete(TrainingContract.ActivityEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    /**
     * Deletes all samples associated with the specified activity from the database.
     *
     * @param activityId The ID of the activity whose samples should be deleted.
     */
    fun deleteSamplesForActivity(activityId: Long) {
        val db = dbHelper.writableDatabase
        val selection = "${TrainingContract.TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID} = ?"
        val selectionArgs = arrayOf(activityId.toString())
        db.delete(TrainingContract.TrainingDataEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
    }
}