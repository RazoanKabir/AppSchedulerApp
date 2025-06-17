package com.example.appschedulerapp.handler.dbhandler

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseBaseClass(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        var DATABASE_VERSION = 1
        var DATABASE_NAME = "AppSchedulerDB"
        var TABLE_SCHEDULER = "AppSchedulerTable"
        var TABLE_SCHEDULER_HISTORY = "AppSchedulerHistoryTable"
        var KEY_ID = "_id"
        var KEY_NAME = "app_name"
        var KEY_PACKAGE_NAME = "package_name"
        var KEY_NOTE = "note"
        var KEY_DATE_TIME = "date_time"
        var KEY_YEAR = "year"
        var KEY_MONTH = "month"
        var KEY_DAY = "day"
        var KEY_HOUR = "hour"
        var KEY_MINUTE = "minute"
        var KEY_SECOND = "second"
        var KEY_IS_REPEATABLE = "is_repeatable"
        var KEY_IS_EXECUTED = "is_executed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_APP_SCHEDULER_TABLE = ("CREATE TABLE " + TABLE_SCHEDULER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_YEAR + " TEXT,"
                + KEY_MONTH + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_HOUR + " TEXT,"
                + KEY_MINUTE + " TEXT,"
                + KEY_SECOND + " TEXT,"
                + KEY_IS_REPEATABLE + " TEXT,"
                + KEY_IS_EXECUTED + " TEXT"
                + ")")
        db?.execSQL(CREATE_APP_SCHEDULER_TABLE)

        val CREATE_APP_SCHEDULER_HISTORY_TABLE = ("CREATE TABLE " + TABLE_SCHEDULER_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_YEAR + " TEXT,"
                + KEY_MONTH + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_HOUR + " TEXT,"
                + KEY_MINUTE + " TEXT,"
                + KEY_SECOND + " TEXT,"
                + KEY_IS_REPEATABLE + " TEXT,"
                + KEY_IS_EXECUTED + " TEXT"
                + ")")
        db?.execSQL(CREATE_APP_SCHEDULER_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULER_HISTORY")
        onCreate(db)
    }
}