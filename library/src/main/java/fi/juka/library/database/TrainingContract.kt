package fi.juka.library.database

import android.provider.BaseColumns

/**
 * TrainingContract defines the contract for the database schema, including table names,
 * column names, and SQL queries for creating and dropping tables related to training data
 * and activities.
 */
object TrainingContract {

    /**
     * TrainingDataEntry defines the schema for the training_data table,
     * including column names and SQL queries for creating and dropping the table.
     */
    object TrainingDataEntry {
        const val TABLE_NAME = "training_data"
        const val COLUMN_NAME_X_AXIS = "x_axis"
        const val COLUMN_NAME_Y_AXIS = "y_axis"
        const val COLUMN_NAME_Z_AXIS = "z_axis"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_ACTIVITY_ID = "activity_id"
        const val COLUMN_NAME_TOTAL_ACCELERATION = "total_acceleration"

        const val SQL_CREATE_TABLE = "CREATE TABLE ${TrainingDataEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${TrainingDataEntry.COLUMN_NAME_TIMESTAMP} LONG, " +
                "${TrainingDataEntry.COLUMN_NAME_X_AXIS} DOUBLE, " +
                "${TrainingDataEntry.COLUMN_NAME_Y_AXIS} DOUBLE, " +
                "${TrainingDataEntry.COLUMN_NAME_Z_AXIS} DOUBLE, " +
                "${TrainingDataEntry.COLUMN_NAME_TOTAL_ACCELERATION} DOUBLE, " +
                "${TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID} REAL, " +
                "FOREIGN KEY(${TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID}) REFERENCES " +
                "${ActivityEntry.TABLE_NAME}(${BaseColumns._ID}))"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${TrainingDataEntry.TABLE_NAME}"
    }

    /**
     * ActivityEntry defines the schema for the activity table,
     * including column names and SQL queries for creating and dropping the table.
     */
    object ActivityEntry {
        const val TABLE_NAME = "activity"
        const val COLUMN_NAME_ACTIVITY = "activity_name"
        const val COLUMN_NAME_SAMPLES = "samples"

        const val SQL_CREATE_TABLE = "CREATE TABLE ${ActivityEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${ActivityEntry.COLUMN_NAME_ACTIVITY} TEXT, " +
                "${ActivityEntry.COLUMN_NAME_SAMPLES} INTEGER DEFAULT 0)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${ActivityEntry.TABLE_NAME}"
    }
}