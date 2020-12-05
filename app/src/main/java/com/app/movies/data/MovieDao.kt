package com.app.movies.data

import androidx.room.*

@Dao
interface MovieDao {
    // the users are gotten sorted by the full name
    @Query("SELECT * from movies_table")
    suspend fun getMovies(): List<Movie>

    @Query("SELECT * FROM movies_table WHERE imdbID LIKE :id")
    suspend fun findMovieWithImdbID(id: String): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)
}