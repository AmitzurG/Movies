package com.app.movies.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.movies.R

class MoviesFragment : Fragment() {

    private lateinit var moviesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.moviesFragmentLabel)
        setMoviesRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movies, menu)
        val searchItem = menu.findItem(R.id.moviesSearchView)
        val moviesSearchView = searchItem?.actionView as? SearchView
        if (moviesSearchView != null) {
            setMoviesSearchView(moviesSearchView)
        }
    }

    private fun setMoviesRecyclerView() = try {
        moviesRecyclerView = requireView().findViewById(R.id.moviesRecyclerView)
        moviesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
            addItemDecoration(dividerItemDecoration)
            adapter = MoviesRecyclerViewAdapter()
            (adapter as MoviesRecyclerViewAdapter).movieItemClicked = {
                (activity as MoviesActivity).moviesViewModel.getMovieByImdbId(it).observe(viewLifecycleOwner, { movie ->
                    val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(
                            movie.poster, movie.title, movie.imdbID, movie.released, movie.runtime, movie.genre, movie.director, movie.writer, movie.actors
                    )
                    findNavController().navigate(action)
                })
            }

            (adapter as MoviesRecyclerViewAdapter).movieFavoriteClicked = { movie, isFavorite ->
                val viewModel = (activity as? MoviesActivity)?.moviesViewModel
                viewModel?.getMovieByImdbId(movie.imdbID)?.observe(viewLifecycleOwner, {
                    if (isFavorite) {
                        viewModel.insertMovieToDatabase(it)
                    } else {
                        viewModel.deleteMovieFromDatabase(it)
                    }
                })
            }
        }
    } catch (e: IllegalStateException) {
        Log.w(MoviesActivity.TAG, "IllegalStateException - error=$e.message")
    }

    private fun setMoviesSearchView(moviesSearchView: SearchView) {
        moviesSearchView.queryHint = getString(R.string.searchMovies)
        if ((activity as MoviesActivity).moviesViewModel.searchText.isNotEmpty()) {
            moviesSearchView.setQuery((activity as MoviesActivity).moviesViewModel.searchText, false)
        }
        moviesSearchView.maxWidth = Integer.MAX_VALUE
        setMoviesSearchViewListener(moviesSearchView)
        val searchText = (activity as MoviesActivity).moviesViewModel.searchText
        if (searchText.isNotEmpty()) {
            moviesSearchView.setQuery(searchText, true)
        }
    }

    private fun setMoviesSearchViewListener(moviesSearchView: SearchView) = moviesSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            moviesSearchView.setQuery("", false)
            moviesSearchView.isIconified = true
            moviesSearchView.setQuery(query, false)
            return true
        }

        override fun onQueryTextChange(searchText: String) : Boolean {
            (activity as MoviesActivity).moviesViewModel.getMovies(searchText).observe(viewLifecycleOwner, {
                (moviesRecyclerView.adapter as? MoviesRecyclerViewAdapter)?.movies = it
            })
            return true
        }
    })
}
