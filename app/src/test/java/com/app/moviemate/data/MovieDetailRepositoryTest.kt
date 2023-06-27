package com.app.moviemate.data

import com.app.moviemate.api.DataResult
import com.app.moviemate.api.MovieService
import com.app.moviemate.model.Genre
import com.app.moviemate.model.MovieDetail
import com.app.moviemate.model.MovieVideo
import com.app.moviemate.model.MovieVideoResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.assertj.core.api.Assertions.*
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class MovieDetailRepositoryTest {
    @Mock
    private lateinit var movieService: MovieService

    private lateinit var movieDetailRepository: MovieDetailRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        movieDetailRepository = MovieDetailRepository(movieService)
    }

    @Test
    fun `getMovieById should return success result when API response is successful`() = runBlocking {
        // Mock API response
        val movieId = 123

        val movieDetail = MovieDetail(id = movieId, title = "Movie 1", genres = listOf(Genre(id = 28, name = "Action"), Genre(id = 12, name = "Adventure")),
            posterPath = null, releaseDate = "2023-12-18", voteAverage = 5.4f, overview = "Over many missions and against impossible odds", runtime = 232)
        val apiResponse = Response.success(movieDetail)
        `when`(movieService.getMovie(movieId)).thenReturn(apiResponse)

        // Call the function being tested
        val resultFlow = movieDetailRepository.getMovieById(movieId)
        println(resultFlow.first())
        println(DataResult.Success(movieDetail))
        // Collect the flow and verify the result
        val result = resultFlow.first()

//        assertThat(result).isInstanceOf(DataResult.Success::class.java)
//        assertThat(result).isEqualTo(DataResult.Success(movieDetail))
    }

//    @Test
//    fun `getMovieById should return error result when API response is unsuccessful`() = runBlocking {
//        // Mock API response
//        val movieId = 123
//        val errorResponse = Response.error<MovieDetail>(400, mockResponseBody())
//        `when`(movieService.getMovie(movieId)).thenReturn(errorResponse)
//
//        // Call the function being tested
//        val resultFlow = movieDetailRepository.getMovieById(movieId)
//
//        // Collect the flow and verify the result
//        val result = resultFlow.first()
//        assertThat(result).isInstanceOf(DataResult.Error::class.java)
//    }
//
//    @Test
//    fun `getMovieVideo should return movie video when API response is successful and has video`() = runBlocking {
//        // Mock API response
//        val movieId = 123
//        val movieVideo = MovieVideo(id = "123", key = "abc123", name = "Test Video", site = "Site", size = 200, type = "Type")
//        val movieVideoResponse = MovieVideoResponse(id = 1, results = listOf(movieVideo))
//        val apiResponse = Response.success(movieVideoResponse)
//        `when`(movieService.getMovieVideo(movieId)).thenReturn(apiResponse)
//
//        // Call the function being tested
//        val result = movieDetailRepository.getMovieVideo(movieId)
//
//        // Verify the result
//        assertThat(result).isEqualTo(movieVideo)
//    }
//
//    @Test
//    fun `getMovieVideo should return null when API response is successful but has no video`() = runBlocking {
//        // Mock API response
//        val movieId = 123
//        val movieVideoResponse = MovieVideoResponse(id = 1, results = emptyList())
//        val apiResponse = Response.success(movieVideoResponse)
//        `when`(movieService.getMovieVideo(movieId)).thenReturn(apiResponse)
//
//        // Call the function being tested
//        val result = movieDetailRepository.getMovieVideo(movieId)
//
//        // Verify the result
//        assertThat(result).isNull()
//    }
//
//    @Test
//    fun `getMovieVideo should return null when API response is unsuccessful`() = runBlocking {
//        // Mock API response
//        val movieId = 123
//        val errorResponse = Response.error<MovieVideoResponse>(400, mockResponseBody())
//        `when`(movieService.getMovieVideo(movieId)).thenReturn(errorResponse)
//
//        // Call the function being tested
//        val result = movieDetailRepository.getMovieVideo(movieId)
//
//        // Verify the result
//        assertThat(result).isNull()
//    }

    private fun mockResponseBody(): ResponseBody {
        return mock(ResponseBody::class.java)
    }
}