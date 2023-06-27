package com.app.moviemate.data

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.moviemate.api.MovieService
import com.app.moviemate.db.dao.MovieDao
import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieType
import retrofit2.HttpException
import java.io.IOException

const val START_PAGE = 1


// służy do paginacji danych filmowych z serwisu API
class MoviePagingSource(
    private val service: MovieService,
    private val movieType: MovieType
) : PagingSource<Int, Movie>() {

    //  wywoływana, gdy wymagane są dane dla określonej strony.
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: START_PAGE

        return try {
            val response = when (movieType) {
                MovieType.NOW_PLAYING -> service.getNowPlayingMovies(page = page)
                MovieType.UPCOMING -> service.getUpcomingMovies(page = page)
                MovieType.TOP_RATED -> service.getTopRatedMovies(page = page)
                else -> throw IllegalArgumentException("Invalid movie type")
            }

            val movies = response.results

            LoadResult.Page(
                data = movies,
                prevKey = if (page == START_PAGE) null else page - 1,
                nextKey = if (movies.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }


    }


    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
