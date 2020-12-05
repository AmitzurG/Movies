package com.app.movies.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies_table")
data class Movie(
        @SerializedName("Title") val title: String,
        @SerializedName("Year") val year: String,
        @PrimaryKey val imdbID: String,
        @SerializedName("Poster") val poster: String,
        @SerializedName("Released") val released: String,
        @SerializedName("Runtime") val runtime: String,
        @SerializedName("Genre") val genre: String,
        @SerializedName("Director") val director: String,
        @SerializedName("Writer") val writer: String,
        @SerializedName("Actors") val actors: String)

