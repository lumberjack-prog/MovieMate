package com.app.moviemate.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.moviemate.db.converters.Converters
import com.google.gson.annotations.SerializedName
import com.app.moviemate.utils.Constants

@Entity(tableName = "favorite_movies")
data class MovieDetail(

    @PrimaryKey
    val id : Int,
    val title : String,
    @field:SerializedName("poster_path")
    val posterPath : String?,
    @field:SerializedName("release_date")
    val releaseDate : String,
    @field:SerializedName("vote_average")
    val voteAverage : Float,
    @TypeConverters(Converters::class)
    @field:SerializedName("genres")
    val genres : List<Genre>,
    @field:SerializedName("overview")
    val overview : String?,
    @field:SerializedName("runtime")
    val runtime : Int
) {

    val posterUrl : String?
        get() = if (posterPath != null) "${Constants.IMAGE_BASE_URL}$posterPath" else null

    val releaseYear : String
        get() = releaseDate.split('-')[0]

    val duration : String
        get() = "${runtime / 60}hr ${runtime % 60}min"

    val rating : String
        get() = "$voteAverage/10"

    override fun toString(): String {
        return "MovieDetail(id=$id, title='$title', posterPath=$posterPath, releaseDate='$releaseDate', voteAverage=$voteAverage, genres=$genres, overview=$overview, runtime=$runtime, posterUrl=$posterUrl, releaseYear='$releaseYear', duration='$duration', rating='$rating')"
    }


}