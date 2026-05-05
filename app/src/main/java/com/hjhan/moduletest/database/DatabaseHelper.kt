package com.hjhan.moduletest.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "moduletest.db"
        private const val DB_VERSION = 4

        const val TABLE_USERS = "users"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PHONE = "phone"
        const val COL_WEBSITE = "website"
        const val COL_ADDR_STREET = "addr_street"
        const val COL_ADDR_SUITE = "addr_suite"
        const val COL_ADDR_CITY = "addr_city"
        const val COL_ADDR_ZIPCODE = "addr_zipcode"
        const val COL_COMPANY_NAME = "company_name"
        const val COL_COMPANY_CATCHPHRASE = "company_catchphrase"
        const val COL_LAST_UPDATED = "last_updated"
        const val COL_IS_FAVORITE = "is_favorite"

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY,
                $COL_NAME TEXT,
                $COL_USERNAME TEXT,
                $COL_EMAIL TEXT,
                $COL_PHONE TEXT,
                $COL_WEBSITE TEXT,
                $COL_ADDR_STREET TEXT,
                $COL_ADDR_SUITE TEXT,
                $COL_ADDR_CITY TEXT,
                $COL_ADDR_ZIPCODE TEXT,
                $COL_COMPANY_NAME TEXT,
                $COL_COMPANY_CATCHPHRASE TEXT,
                $COL_LAST_UPDATED INTEGER,
                $COL_IS_FAVORITE INTEGER DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_PHONE TEXT")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_WEBSITE TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_IS_FAVORITE INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_LAST_UPDATED INTEGER DEFAULT 0")
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_USERNAME TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_ADDR_STREET TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_ADDR_SUITE TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_ADDR_CITY TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_ADDR_ZIPCODE TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_COMPANY_NAME TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COL_COMPANY_CATCHPHRASE TEXT")
        }
    }
}