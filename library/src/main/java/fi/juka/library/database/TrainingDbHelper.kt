package fi.juka.library.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * TrainingDbHelper is a helper class to manage database creation and version management.
 * It extends SQLiteOpenHelper and provides methods to create and upgrade the database,
 * as well as a dangerous method to delete all tables in the database.
 *
 * @property context The context used to create the helper.
 * @constructor Creates an instance of TrainingDbHelper with the given context.
 */
class TrainingDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Called when the database is created for the first time.
     *
     * @param db The database.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TrainingContract.ActivityEntry.SQL_CREATE_TABLE)
        db.execSQL(TrainingContract.TrainingDataEntry.SQL_CREATE_TABLE)
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(TrainingContract.TrainingDataEntry.SQL_DROP_TABLE)
        db.execSQL(TrainingContract.ActivityEntry.SQL_DROP_TABLE)
        onCreate(db)
    }

    /**
     * Deletes all tables in the database.
     * Warning: Use this function with caution as it deletes all data in the database.
     */
    fun deleteAllTables() {
        val db = writableDatabase
        db.execSQL(TrainingContract.TrainingDataEntry.SQL_DROP_TABLE)
        db.execSQL(TrainingContract.ActivityEntry.SQL_DROP_TABLE)
        onCreate(db)
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Training.db"
    }
}