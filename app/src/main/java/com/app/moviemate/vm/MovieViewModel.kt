package com.app.moviemate.vm

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.moviemate.api.MovieService
import com.app.moviemate.data.MoviePagingSource
import com.app.moviemate.data.MovieRepository
import com.app.moviemate.db.dao.MovieDao
import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val repository : MovieRepository) : ViewModel() {

    private var currentMovieType = MovieType.NOW_PLAYING
    private var currentMovies : Flow<PagingData<Movie>>? = null

    @ExperimentalPagingApi
    fun getMoviesByType(newMovieType : MovieType) : Flow<PagingData<Movie>> {

        val latestResult = currentMovies
        if (currentMovieType == newMovieType && latestResult != null) {
            return latestResult
        }

        currentMovieType = newMovieType
        val newResult = repository.getMoviesByType(newMovieType)
            .cachedIn(viewModelScope)
        currentMovies = newResult
        return newResult
    }

    fun convertToPagingData(
        movieList: List<Movie>
    ): Flow<PagingData<Movie>> {
        return flowOf(movieList)
            .map { PagingData.from(it) }
            .cachedIn(viewModelScope) // buforuje PagingData
    }

}