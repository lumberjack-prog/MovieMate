package com.app.moviemate.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.moviemate.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieDetail)

    @Delete
    suspend fun deleteMovie(movie: MovieDetail)

    @Query("SELECT * FROM favorite_movies")
    fun getAllMovies(): LiveData<List<MovieDetail>>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    fun getMovieById(movieId: Int): LiveData<MovieDetail?>
}

