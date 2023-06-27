package com.app.moviemate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.moviemate.R

// odpowiedzialna za zarządzanie stanem ładowania w adapterze listy filmów
class MovieLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<MovieLoadStateViewHolder>() {
     // funkcja retry jest wywoływana przy ponownej próbie załadowania danych

    override fun onBindViewHolder(holder: MovieLoadStateViewHolder, loadState: LoadState) {
        //  przypisuje obiekt LoadState do widoku
        holder.bind(loadState)
    }
    // tworzy nowy obiekt MovieLoadStateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MovieLoadStateViewHolder {
        return MovieLoadStateViewHolder.create(parent, retry)
    }
}
// reprezentuje pojedynczy element w interfejsie użytkownika który wyświetla stan ładowania w
// adapterze listy filmów
class MovieLoadStateViewHolder(view : View, retry : () -> Unit) : RecyclerView.ViewHolder(view) {

    private val imgRefresh = view.findViewById<ImageView>(R.id.img_refresh)
    private val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

    init {
        view.setOnClickListener { retry.invoke() }
    }

    // dostosowuje widok na podstawie stanu ładowania
    fun bind(loadState : LoadState) {
        progressBar.isVisible = loadState is LoadState.Loading
        imgRefresh.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        //tworzy nowy obiekt MovieLoadStateViewHolder,
        fun create(parent : ViewGroup, retry: () -> Unit) : MovieLoadStateViewHolder {
            //tworzy widok na podstawie pliku XML
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movies_load_state_footer_view,parent,false)
            return MovieLoadStateViewHolder(view,retry)
        }
    }
}