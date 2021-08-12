package sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import modal.user
import java.util.*
import kotlin.collections.ArrayList
import java.util.List as List2
import kotlin.collections.List as List1

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")")

    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USER_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //Drop User Table if it is already exits
        db.execSQL(DROP_USER_TABLE)

        // if not then  it their then it done again
        onCreate(db)

    }

    fun getAllUser(): List2 {

        // array of columns to fetch
        val columns =
            arrayOf(COLUMN_USER_ID, COLUMN_USER_EMAIL, COLUMN_USER_NAME, COLUMN_USER_PASSWORD)

        // sorting orders
        val sortOrder = "$COLUMN_USER_NAME ASC"
        val userList = arrayListOf()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_USER,
            columns,
            null, null, null, null, sortOrder
        )
        if (cursor.moveToFirst()) {
            do {
                val user = user(
                    id = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)).toInt(),
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                    email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                    password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD))
                )

                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }



    fun addUser(user: user) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)

        // To insert into the row
        db.insert(TABLE_USER, null, values)
        db.close()
    }

    fun updateUser(user: user) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)

        db.update(
            TABLE_USER, values, "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString())
        )
        db.close()
    }

    fun deleteUser(user: user) {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(
            TABLE_USER, "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString())
        )
        db.close()


    }

    // validation are check here
    fun checkUser(email: String): Boolean {


        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(
            TABLE_USER, columns, selection, selectionArgs, null, null, null
        )


        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }
        return false
    }
    // to check the user is already present or not

    fun checkUser(email: String, password: String): Boolean {

        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(
            TABLE_USER, columns, selection, selectionArgs, null, null, null
        )

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0)
            return true
        return false

    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "UserManager.db"
        private val TABLE_USER = "user"
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_NAME = "user_name"
        private val COLUMN_USER_EMAIL = "user_email"
        private val COLUMN_USER_PASSWORD = "user_password"
    }
}