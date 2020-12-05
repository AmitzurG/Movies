package com.app.movies.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.app.movies.data.Movie
import com.app.movies.data.MoviesDatabase
import com.app.movies.data.MoviesNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    var searchText = ""
    private val movieDao = MoviesDatabase.getDatabase(application).movieDao()

    // the favorite movies are stored in the database
    val favoriteMovies = liveData(Dispatchers.IO) {
        val favoriteMovies = movieDao.getMovies()
        emit(favoriteMovies)
    }

    fun movieWithimdbIdFromDatabase(imdbId: String) = liveData(Dispatchers.IO) {
        val movies = movieDao.findMovieWithImdbID(imdbId)
        emit(movies)
    }

    fun insertMovieToDatabase(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        movieDao.insert(movie)
    }

    fun deleteMovieFromDatabase(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        movieDao.delete(movie)
    }

    // get the searched movies from the network
    fun getMovies(searchMovie: String) = liveData(Dispatchers.IO) {
        searchText = searchMovie
        val movies = MoviesNetwork.getMovies(searchMovie)
        emit(movies)
    }

    fun getMovieByImdbId(imdbId: String) = liveData(Dispatchers.IO) {
        val movie = MoviesNetwork.movieWithImdbId(imdbId)
        emit(movie)
    }
}

class MoviesViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MoviesViewModel(application) as T
    }
}