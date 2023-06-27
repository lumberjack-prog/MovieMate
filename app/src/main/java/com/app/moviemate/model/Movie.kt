package com.app.moviemate.model

import com.app.moviemate.utils.Constants.IMAGE_BASE_URL
import com.google.gson.annotations.SerializedName

data class Movie(
    val id : Int,
    val title : String,
    @field:SerializedName("poster_path")
    val posterPath : String?,
    @field:SerializedName("release_date")
    val releaseDate : String,
    @field:SerializedName("vote_average")
    val voteAverage : Float
) {

    val posterUrl : String?
        get() = if (posterPath != null) "$IMAGE_BASE_URL$posterPath" else null

    override fun toString(): String {
        return "Movie(id=$id, title='$title', posterPath=$posterPath, releaseDate='$releaseDate', voteAverage=$voteAverage, posterUrl=$posterUrl)"
    }


}