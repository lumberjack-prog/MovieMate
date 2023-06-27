package com.app.moviemate.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryTest {

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `getMoviesByType should return flow of PagingData`() = runBlocking {
        // Utwórz listę filmów
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                releaseDate = "2023-05-17",
                posterPath = null,
                voteAverage = 5.1f
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                releaseDate = "2023-05-18",
                posterPath = null,
                voteAverage = 7.2f
            ),
            Movie(
                id = 3,
                title = "Movie 3",
                releaseDate = "2023-12-18",
                posterPath = null,
                voteAverage = 9.5f
            ),
        )

        // Utwórz przepływ danych stronicowania za pomocą listy filmów
        val pagingData: PagingData<Movie> = PagingData.from(movies)
        val flow: Flow<PagingData<Movie>> = flowOf(pagingData)

        // Mock z zachowania MovieRepository
        `when`(movieRepository.getMoviesByType(MovieType.NOW_PLAYING)).thenReturn(flow)

        // Wywołaj testowaną metodę
        val resultFlow = movieRepository.getMoviesByType(MovieType.NOW_PLAYING)

        // Zbierz przepływ i potwierdź wynik
        val result = resultFlow.single()

        assertEquals(pagingData, result)
    }
}