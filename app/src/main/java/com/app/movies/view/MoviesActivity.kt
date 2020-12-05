package com.app.movies.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.movies.R
import com.app.movies.viewmodel.MoviesViewModel
import com.app.movies.viewmodel.MoviesViewModelFactory

class MoviesActivity : AppCompatActivity() {
    companion object {
        const val TAG = "Movies"
    }

    lateinit var moviesViewModel: MoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)
        setSupportActionBar(findViewById(R.id.toolbar))
        moviesViewModel = ViewModelProvider(this, MoviesViewModelFactory(application)).get(MoviesViewModel::class.java)
    }
}