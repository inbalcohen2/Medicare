package com.example.pillreminder.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.pillreminder.models.PillModel

class DataBaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null, DATABASE_VERSION){

        companion object{
            private const val DATABASE_VERSION =1
            private const val TABLE_PILL ="PillsTable"
            private const val DATABASE_NAME = "PillsDatabase"


            //All the Columns pill name

            private const val KEY_ID= "_id"
            private const val KEY_NAME="name"
            private const val KEY_IMAGE="image"
            private const val KEY_DESCRIPTION="description"
            private const val KEY_DATE="date"
            private const val KEY_TIME="time"
            private const val KEY_LATITUDE="latitude"
            private const val KEY_LONGITUDE="longitude"

        }

    override fun onCreate(db: SQLiteDatabase?) {
        createTablePill(db)
    }

    private fun createTablePill(db: SQLiteDatabase?) {
        val CREATE_TABLE_PILL = ("CREATE TABLE "+TABLE_PILL+"("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")

        db?.execSQL(CREATE_TABLE_PILL)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PILL")
        onCreate(db)

    }



    fun addPill(pill:PillModel) :Long{

        val db =this.writableDatabase

        val contentValues=ContentValues()
        contentValues.put(KEY_NAME ,pill.name)
        contentValues.put(KEY_IMAGE,pill.image)
        contentValues.put(KEY_DESCRIPTION ,pill.description)
        contentValues.put(KEY_DATE,pill.date)
        contentValues.put(KEY_TIME ,pill.time)
        contentValues.put(KEY_LATITUDE,pill.latitude)
        contentValues.put(KEY_LONGITUDE ,pill.longitude)

        // Inserting Row
        val result = db.insert(TABLE_PILL, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return result

    }

    public fun getPillList():ArrayList<PillModel>{

        val pillList = ArrayList<PillModel>()
        val selectQuery ="SELECT * FROM $TABLE_PILL"

        val db =this.readableDatabase
        try {
            val cursor : Cursor =db.rawQuery(selectQuery,null)
            if(cursor.moveToFirst()){
                do {
                    val pill = PillModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                    pillList.add(pill)


                }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e:SQLiteException){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return pillList


    }


}