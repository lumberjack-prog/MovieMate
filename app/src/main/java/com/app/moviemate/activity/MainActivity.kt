package com.app.moviemate.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.GridLayoutManager
import com.app.moviemate.R
import com.app.moviemate.adapter.MovieLoadStateAdapter
import com.app.moviemate.adapter.MoviesAdapter
import com.app.moviemate.data.MovieRepository
import com.app.moviemate.databinding.ActivityMainBinding
import com.app.moviemate.db.MovieDatabase
import com.app.moviemate.model.Movie
import com.app.moviemate.model.MovieDetail
import com.app.moviemate.model.MovieResponse
import com.app.moviemate.model.MovieType
import com.app.moviemate.vm.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MovieViewModel by viewModels()
    private val movieAdapter = MoviesAdapter { movie: Movie? ->
        movie?.let {
            val intent = Intent(this@MainActivity, MovieDetailActivity::class.java).apply {
                putExtra("movie_id", it.id)
            }
            startActivity(intent)
        }
    }
    private lateinit var binding: ActivityMainBinding
    private var movieJob: Job? = null //  wykorzystywany do zarządzania operacjami asynchronicznymi.

    // tworzenia menu w głównym interfejsie
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // wywoływana podczas tworzenia aktywności.
    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        setUpAdapter()
        setUpView()
        observeMovieList(
            MovieType.NOW_PLAYING,
            this,
            this
        )  // Metoda observeMovieList() odpowiada za obserwację listy filmów na podstawie podanego typu filmu.
    }

    // ustawia interakcje i wygląd widoku
    private fun setUpView() {

        binding.btnRetry.setOnClickListener {
            movieAdapter.retry()
        }

        binding.rvMovie.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = movieAdapter.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter { movieAdapter.retry() },
                footer = MovieLoadStateAdapter { movieAdapter.retry() })
        }
    }
    //ustawia nasłuchiwanie stanu załadowania w adapterze
    private fun setUpAdapter() {
        // widoczność elementów w zależności od stanu
        movieAdapter.addLoadStateListener { loadState ->
            binding.rvMovie.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.btnRetry.isVisible = loadState.source.refresh is LoadState.Error
        }

    }

    //odpowiedzialna za obserwowanie listy filmów na podstawie podanego typu filmu
    @ExperimentalPagingApi
    private fun observeMovieList(
        movieType: MovieType,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {

        supportActionBar?.title = "${movieType.typeName} Movies"
        movieJob?.cancel()
        movieJob = lifecycleScope.launch {

            if (movieType == MovieType.FAVORITE_MOVIES) {
                val movieDao = MovieDatabase.getDatabase(applicationContext).movieDao()
                val movieListLiveData: LiveData<List<MovieDetail>> = movieDao.getAllMovies()

                movieListLiveData.observe(lifecycleOwner) { movieList ->
                    if (movieList.isNotEmpty()) {
                        val movies = movieList.map { movieDetail ->
                            Movie(
                                movieDetail.id,
                                movieDetail.title,
                                movieDetail.posterPath,
                                movieDetail.releaseDate,
                                movieDetail.voteAverage
                            )
                        }

                        lifecycleScope.launch {
                            viewModel.convertToPagingData(movies).collectLatest { pagingData ->
                                movieAdapter.submitData(pagingData)
                            }
                        }
                    } else {

                        lifecycleScope.launch {
                            observeMovieList(MovieType.NOW_PLAYING, context, lifecycleOwner)
                            viewModel.getMoviesByType(MovieType.NOW_PLAYING)
                                .collectLatest { pagingData ->
                                    movieAdapter.submitData(pagingData)
                                }
                        }
                    }
                }

            } else {
                //zwraca strumień (Flow) danych filmów, które są następnie przekazywane do adaptera
                viewModel.getMoviesByType(movieType)
                    .collectLatest { pagingData ->
                        movieAdapter.submitData(pagingData)
                    }
            }
        }
    }

    // Jest wywoływana, gdy użytkownik wybierze element z menu
    @ExperimentalPagingApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val movieType = when (item.itemId) {
            R.id.now_playing -> MovieType.NOW_PLAYING
            R.id.upcoming -> MovieType.UPCOMING
            R.id.top_rated -> MovieType.TOP_RATED
            R.id.favorite_movies -> MovieType.FAVORITE_MOVIES
            else -> MovieType.NOW_PLAYING // Now playing movie type is default
        }
        observeMovieList(movieType, this, this)
        return super.onOptionsItemSelected(item)
    }
}