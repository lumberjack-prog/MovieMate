package com.app.moviemate.vm

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.*
import com.app.moviemate.api.DataResult
import com.app.moviemate.data.MovieDetailRepository
import com.app.moviemate.model.MovieDetail
import com.app.moviemate.model.MovieVideo
import com.app.moviemate.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel // że może być wstrzykiwana zależność przez bibliotekę Hilt
class MovieDetailViewModel @Inject constructor(
    private val repository : MovieDetailRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), LifecycleObserver {

    private val movieId : Int by lazy {
        savedStateHandle.get<Int>("movie_id")!!
    }

    private val _eventMovieVideo = MutableLiveData<Event<MovieVideo>>()

    // służy do obserwowania zdarzenia MovieVideo
    val eventMovieVideo : LiveData<Event<MovieVideo>>
        get() = _eventMovieVideo

    /**
     * funkcja [liveData] obsługuje strumienia danych z repozytorium
     */
    val movieDetailResult : LiveData<DataResult<MovieDetail>> = liveData {
        repository.getMovieById(movieId)
            .collect { emit(it) }
    }

    // wywoływana w celu otwarcia filmu
    fun openMovieTrailer(context: Context) {
        /**
         * Wykorzystuje [viewModelScope] do uruchomienia zadania w kontekście ViewModel.
         */
        viewModelScope.launch {
            repository.getMovieVideo(movieId)?.let { movieVideo ->
                // Jeśli jest dostępne, zostaje wysłane jako zdarzenie
                _eventMovieVideo.postValue(Event(movieVideo))

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movieVideo.movieUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Handle the case where the YouTube app or web browser is not installed
                    // or there is no app available to handle the intent
                    e.printStackTrace()
                }

            } ?: Log.d("Movie video","Trailer cannot be play")
        }

    }

}