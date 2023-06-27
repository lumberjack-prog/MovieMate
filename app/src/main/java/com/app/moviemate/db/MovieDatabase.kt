package com.app.moviemate.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.moviemate.db.converters.Converters
import com.app.moviemate.db.dao.MovieDao
import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieDetail

@Database(entities = [MovieDetail::class], version = 1)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}




