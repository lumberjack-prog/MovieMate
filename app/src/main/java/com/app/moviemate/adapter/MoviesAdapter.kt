package com.app.moviemate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.moviemate.R
import com.app.moviemate.databinding.LayoutMovieItemBinding
import com.app.moviemate.model.Movie

//  jest adapterem używanym w RecyclerView do wyświetlania
//  listy filmów oraz zapewnia obsługę stronicowania danych
class MoviesAdapter(private val itemClickListener : (Movie?) -> Unit)
    : PagingDataAdapter<Movie, MoviesAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            holder.bind(movie)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutMovieItemBinding.inflate(layoutInflater, parent,false)
        return MovieViewHolder(binding)
    }
    // reprezentuje pojedynczy element listy filmów
    inner class MovieViewHolder(private val binding : LayoutMovieItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // definiowane jest zachowanie po kliknięciu na element listy
            binding.root.setOnClickListener { itemClickListener.invoke(getItem(adapterPosition)) }
        }
        // jest odpowiedzialna za ustawienie danych filmu w widoku ViewHoldera
        fun bind(movie : Movie) {
            binding.tvMovie.text = movie.title
            binding.imgPoster.load(movie.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.poster_placeholder)
                error(R.drawable.poster_placeholder)
            }
        }

    }

    companion object {

        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {

            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}