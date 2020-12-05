package com.app.movies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        // singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var database: MoviesDatabase? = null

        fun getDatabase(context: Context): MoviesDatabase {
            if (database != null) {
                return database as MoviesDatabase
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, MoviesDatabase::class.java, "movies_database").build()
                database = instance
                return instance
            }
        }
    }
}