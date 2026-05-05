package com.hjhan.moduletest.database

import android.content.ContentValues
import com.hjhan.moduletest.model.Address
import com.hjhan.moduletest.model.Company
import com.hjhan.moduletest.model.User

class UserDao(private val dbHelper: DatabaseHelper) {

    fun getAllUsers(): List<User> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_USERS, null, null, null, null, null, null)
        return cursor.use { c ->
            buildList {
                while (c.moveToNext()) add(c.toUser())
            }
        }
    }

    fun getUserById(id: Int): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use { c -> if (c.moveToFirst()) c.toUser() else null }
    }

    fun saveUsers(users: List<User>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            users.forEach { db.insertOrReplace(it) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun saveUser(user: User) {
        dbHelper.writableDatabase.insertOrReplace(user)
    }

    fun updateFavorite(userId: Int, isFavorite: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_IS_FAVORITE, if (isFavorite) 1 else 0)
        }
        db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun getFavoriteUsers(): List<User> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_IS_FAVORITE} = 1",
            null, null, null, null
        )
        return cursor.use { c ->
            buildList {
                while (c.moveToNext()) add(c.toUser())
            }
        }
    }

    private fun android.database.sqlite.SQLiteDatabase.insertOrReplace(user: User) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_ID, user.id)
            put(DatabaseHelper.COL_NAME, user.name)
            put(DatabaseHelper.COL_USERNAME, user.username)
            put(DatabaseHelper.COL_EMAIL, user.email)
            put(DatabaseHelper.COL_PHONE, user.phone)
            put(DatabaseHelper.COL_WEBSITE, user.website)
            put(DatabaseHelper.COL_ADDR_STREET, user.address?.street)
            put(DatabaseHelper.COL_ADDR_SUITE, user.address?.suite)
            put(DatabaseHelper.COL_ADDR_CITY, user.address?.city)
            put(DatabaseHelper.COL_ADDR_ZIPCODE, user.address?.zipcode)
            put(DatabaseHelper.COL_COMPANY_NAME, user.company?.name)
            put(DatabaseHelper.COL_COMPANY_CATCHPHRASE, user.company?.catchPhrase)
            put(DatabaseHelper.COL_LAST_UPDATED, user.lastUpdated)
            put(DatabaseHelper.COL_IS_FAVORITE, if (user.isFavorite) 1 else 0)
        }
        insertWithOnConflict(
            DatabaseHelper.TABLE_USERS,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    private fun android.database.Cursor.toUser(): User {
        val street = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_STREET))
        val suite = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_SUITE))
        val city = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_CITY))
        val zipcode = getString(getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_ZIPCODE))
        val address = if (street != null) Address(street, suite ?: "", city ?: "", zipcode ?: "") else null

        val companyName = getString(getColumnIndexOrThrow(DatabaseHelper.COL_COMPANY_NAME))
        val catchPhrase = getString(getColumnIndexOrThrow(DatabaseHelper.COL_COMPANY_CATCHPHRASE))
        val company = if (companyName != null) Company(companyName, catchPhrase ?: "") else null

        return User(
            id = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
            name = getString(getColumnIndexOrThrow(DatabaseHelper.COL_NAME)) ?: "",
            username = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)) ?: "",
            email = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)) ?: "",
            phone = getString(getColumnIndexOrThrow(DatabaseHelper.COL_PHONE)),
            website = getString(getColumnIndexOrThrow(DatabaseHelper.COL_WEBSITE)),
            address = address,
            company = company,
            lastUpdated = getLong(getColumnIndexOrThrow(DatabaseHelper.COL_LAST_UPDATED)),
            isFavorite = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_IS_FAVORITE)) == 1
        )
    }
}