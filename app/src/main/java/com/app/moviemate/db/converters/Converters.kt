package com.app.moviemate.db.converters

import androidx.room.TypeConverter
import com.app.moviemate.model.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<Genre> {
        val listType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Genre>): String {
        return Gson().toJson(list)
    }
}