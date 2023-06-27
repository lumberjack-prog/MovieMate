package com.app.moviemate.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import coil.load
import com.app.moviemate.LoadingDialog
import com.app.moviemate.R
import com.app.moviemate.api.DataResult
import com.app.moviemate.databinding.ActivityMovieDetailBinding
import com.app.moviemate.db.MovieDatabase
import com.app.moviemate.model.MovieDetail
import com.app.moviemate.model.MovieVideo
import com.app.moviemate.utils.EventObserver
import com.app.moviemate.utils.createChip
import com.app.moviemate.vm.MovieDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailActivity : AppCompatActivity() {

    private val movieDetailViewModel : MovieDetailViewModel by viewModels()
    private lateinit var binding : ActivityMovieDetailBinding
    private var loadingDialog : LoadingDialog? = null
    private lateinit var favoriteButton: ImageView
    private var isFavorite: Boolean = false;

    // jest wywoływana podczas tworzenia aktywności
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        favoriteMovies()
        observeMovieDetail()
        observeMovieVideo()

    }

    private fun favoriteMovies() {
        favoriteButton = findViewById(R.id.favorite_button)

        favoriteButton.setOnClickListener {
            // obsługuj ulubione wydarzenie kliknięcia przycisku
            // Przełącz ulubiony stan i odpowiednio zaktualizuj interfejs użytkownika
            isFavorite = !isFavorite

//            val movieDao = MovieDatabase.getDatabase(applicationContext).movieDao()
//            val movieListLiveData: LiveData<List<MovieDetail>> = movieDao.getAllMovies()
//
//            movieListLiveData.observe(this) {movieList ->
//                if (movieList.isNotEmpty()) {
//                    val movieDetail: MovieDetail = movieList[0]
//                    Log.d("TAG", movieDetail.title)
//                }
//            }

            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                // Dodaj film do ulubionych
                val dataResult = movieDetailViewModel.movieDetailResult.value
                // Sprawdź, czy wynik się powiódł i wyodrębnij obiekt szczegółów filmu
                if (dataResult is DataResult.Success) {
                    val movieDetail = dataResult.result
                    // Pobierz film Dao z bazy MovieDatabase
                    val movieDao = MovieDatabase.getDatabase(applicationContext).movieDao()
                    // Uruchom nowy coroutine w ramach bieżącego cyklu życia lub ViewModel
                    lifecycleScope.launch {
                        // Wywołaj funkcję Wstaw film z poziomu coroutine
                        movieDao.insertMovie(movieDetail)
                    }

                    val layout = layoutInflater.inflate(R.layout.custom_toast_layout, findViewById(R.id.custom_toast_layout))
                    val toastText = layout.findViewById<TextView>(R.id.toast_text)
                    toastText.text = "The \"${movieDetail.title}\" movie has been added to favorites"
                    val toast = Toast(applicationContext)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = layout
                    toast.show()


                }
            } else {
                favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
                // Usuń film z ulubionych
                val dataResult = movieDetailViewModel.movieDetailResult.value
                if (dataResult is DataResult.Success) {
                    val movieDetail = dataResult.result
                    val movieDao = MovieDatabase.getDatabase(applicationContext).movieDao()
                    lifecycleScope.launch {
                        movieDao.deleteMovie(movieDetail)
                    }

                    val layout = layoutInflater.inflate(R.layout.custom_toast_layout, findViewById(R.id.custom_toast_layout))
                    val toastText = layout.findViewById<TextView>(R.id.toast_text)
                    toastText.text = "The \"${movieDetail.title}\" movie has been removed from favorites"
                    val toast = Toast(applicationContext)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = layout
                    toast.show()

                }
            }
        }
    }

    private fun observeMovieVideo() {
        // Obserwuj wydarzenie movieVideo
        movieDetailViewModel.eventMovieVideo.observe(this, EventObserver{ movieVideo ->
            movieVideo.movieUrl?.let {
                openTrailer(it)
            }
        })
    }

    //ustawia pasek narzędzi (toolbar) dla aktywności
    private fun setupToolbar() {
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // ustawia przycisk cofania w pasku narzędzi
    }

    // obserwuje rezultat szczegółów filmu
    private fun observeMovieDetail() {

        movieDetailViewModel.movieDetailResult.observe(this) { result ->
            when (result) {
                DataResult.Loading -> {
                    showLoading()
                }

                is DataResult.Error -> {
                    dismissLoading()
                    showError("Loading movie detail failed!")
                }

                is DataResult.Success -> {
                    dismissLoading()
                    showDetail(result.result)
                }
            }
        }
    }
    // wywoływana w przypadku sukcesu otrzymania szczegółów filmu
    private fun showDetail(movieDetail : MovieDetail) {

        val movieDao = MovieDatabase.getDatabase(applicationContext).movieDao()
        val movieDetailLiveData: LiveData<MovieDetail?> = movieDao.getMovieById(movieDetail.id)

        movieDetailLiveData.observe(this) { movieDetail ->
            movieDetail?.let { nonNullMovieDetail ->
                isFavorite = true;
                favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
            }
        }

        binding.tvTitle.text = movieDetail.title
        binding.tvYear.text = movieDetail.releaseYear
        binding.tvDuration.text = movieDetail.duration
        binding.tvRating.text = movieDetail.rating
        binding.tvOverview.text = movieDetail.overview

        binding.imgPoster.load(movieDetail.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
        }

        for(genre in movieDetail.genres) {
            val chip = createChip(genre.name)
            binding.chipGroup.addView(chip)
        }

        // TODO: Implement a functionality to watch trailers
        binding.fabPlay.setOnClickListener {
           movieDetailViewModel.openMovieTrailer(this)
        }
    }

    private fun showLoading() {
        loadingDialog?.show()
    }

    private fun dismissLoading() {
        loadingDialog?.dismiss()
    }

    // wyświetla okno dialogowe z komunikatem błędu
    private fun showError(message : String) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_AlertDialog)
            .setTitle("Error")
            .setMessage(message)
            .setNegativeButton("Cancel") { dialog , _ -> }
            .setPositiveButton("OK") { dialog , _ ->
                dialog.dismiss()
            }.show()
    }

    // Ta metoda otwiera przeglądarkę internetową z podanym adresem URL
    @SuppressLint("QueryPermissionsNeeded")
    private fun openTrailer(url : String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    // obsługuje zdarzenia kliknięcia elementów menu na pasku narzędzi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}