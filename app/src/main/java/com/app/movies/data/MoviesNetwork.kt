package com.app.movies.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object MoviesNetwork {
    const val apiKey = "dce24c91"
    private const val baseUrl = "http://www.omdbapi.com"
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    private val moviesService = retrofit.create(MoviesService::class.java)

    suspend fun getMovies(movieSearch: String): List<Movie> {
        val moviesJsonObject = moviesService.getMovies(movieSearch)
        val moviesJsonArray = moviesJsonObject.get("Search")?.asJsonArray
        return if (moviesJsonArray == null) emptyList() else Gson().fromJson(moviesJsonArray, object : TypeToken<List<Movie>>() {}.type)
    }

    suspend fun movieWithImdbId(imdbId: String): Movie = moviesService.movieWithImdbId(imdbId)
}

private interface MoviesService {
    @GET("/?apiKey=${MoviesNetwork.apiKey}")
    suspend fun getMovies(@Query("s") search: String): JsonObject

    @GET("/?apiKey=${MoviesNetwork.apiKey}")
    suspend fun movieWithImdbId(@Query("i") imdbId: String): Movie
}