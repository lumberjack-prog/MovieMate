package com.app.moviemate.api

import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieDetail
import com.app.moviemate.model.MovieResponse
import com.app.moviemate.model.MovieVideoResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieService {

    @GET("$BASE_URL/movie/now_playing?api_key=$API_KEY")
    suspend fun getNowPlayingMovies(@Query("page")
        page : Int = 1 ) : MovieResponse

    @GET("$BASE_URL/movie/upcoming?api_key=$API_KEY")
    suspend fun getUpcomingMovies(@Query("page")
        page : Int = 1 ) : MovieResponse

    @GET("$BASE_URL/movie/top_rated?api_key=$API_KEY")
    suspend fun getTopRatedMovies(@Query("page")
        page : Int = 1 ) : MovieResponse

    @GET("$BASE_URL/movie/{movieId}?api_key=$API_KEY")
    suspend fun getMovie(@Path("movieId") movieId : Int) : Response<MovieDetail>

    @GET("$BASE_URL/movie/{movieId}?api_key=$API_KEY")
    suspend fun testMovie(@Path("movieId") movieId : Int) : Flow<DataResult<Movie>>


    @GET("$BASE_URL/movie/{movieId}?api_key=$API_KEY")
    suspend fun dddd(@Path("movieId") movieId : Int) : Flow<DataResult<Movie>>


    @GET("$BASE_URL/movie/{movie_id}/videos?api_key=$API_KEY")
    suspend fun getMovieVideo(@Path("movie_id") movieId: Int) : Response<MovieVideoResponse>

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "7cf70ced1695324e07e92da29a83c2fc"
    }
}

sealed class DataResult<out T> {

    object Loading : DataResult<Nothing>()
    data class Success<T>(val result : T) : DataResult<T>()
    data class Error(val error : String) : DataResult<Nothing>()
}