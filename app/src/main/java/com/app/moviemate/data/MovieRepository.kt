package com.app.moviemate.data

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.moviemate.api.MovieService
import com.app.moviemate.db.dao.MovieDao
import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieDetail
import com.app.moviemate.model.MovieType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val service : MovieService,
) {

    @ExperimentalPagingApi
    fun getMoviesByType(movieType : MovieType)
    : Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(service, movieType) }
        ).flow
    }


//
//    @ExperimentalPagingApi
//    fun getMoviesList() : Flow<PagingData<Movie>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 1,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { MoviePagingSource(service) }
//        ).flow
//    }
//
    @ExperimentalPagingApi
    suspend fun getMovieById(movieId : Int) : MovieDetail? {

        return try {
            val response = service.getMovie(movieId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                null
            }
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    fun convertListToPagingData(list: List<MovieDetail>): Flow<PagingData<MovieDetail>> {
        return Pager(config = PagingConfig(pageSize = list.size)) {
            // Create a DataSource based on the list of MovieDetail
            MovieListDataSource(list)
        }.flow
    }
}

class MovieListDataSource(private val list: List<MovieDetail>) : PagingSource<Int, MovieDetail>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDetail> {
        val position = params.key ?: 0
        return LoadResult.Page(
            data = list,
            prevKey = null,
            nextKey = if (position < list.size) position + 1 else null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDetail>): Int? {
        TODO("Not yet implemented")
    }
}