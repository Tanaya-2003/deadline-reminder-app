package com.example.deadlinereminder.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DeadlineDbHelper(context: Context) :
    SQLiteOpenHelper(context, "deadlines.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE deadlines (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "dueDate TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS deadlines")
        onCreate(db)
    }

    fun insertDeadline(title: String, dueDate: String) {
        val values = ContentValues().apply {
            put("title", title)
            put("dueDate", dueDate)
        }
        writableDatabase.insert("deadlines", null, values)
    }

    fun getAllDeadlines(): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM deadlines", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val date = cursor.getString(2)
            list.add(Pair(id, "$title - $date"))
        }

        cursor.close()
        return list
    }

    fun deleteById(id: Int) {
        writableDatabase.delete("deadlines", "id=?", arrayOf(id.toString()))
    }
}
