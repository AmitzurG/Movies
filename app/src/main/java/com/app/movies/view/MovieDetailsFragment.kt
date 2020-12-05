package com.app.movies.view

import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.movies.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class MovieDetailsFragment : Fragment() {
    private val args by navArgs<MovieDetailsFragmentArgs>()

    private val posterImageView by lazy { view?.findViewById<ImageView>(R.id.posterImageView) }
    private val favoriteCheckBox by lazy { view?.findViewById<CheckBox>(R.id.favoriteCheckBox) }
    private val titleTextView by lazy { view?.findViewById<TextView>(R.id.titleTextView) }
    private val imdbIdTextView by lazy { view?.findViewById<TextView>(R.id.imdbIdTextView) }
    private val releasedTextView by lazy { view?.findViewById<TextView>(R.id.releasedTextView) }
    private val runtimeTextView by lazy { view?.findViewById<TextView>(R.id.runtimeTextView) }
    private val genreTextView by lazy { view?.findViewById<TextView>(R.id.genreTextView) }
    private val directorTextView by lazy { view?.findViewById<TextView>(R.id.directorTextView) }
    private val writerTextView by lazy { view?.findViewById<TextView>(R.id.writerTextView) }
    private val actorsTextView by lazy { view?.findViewById<TextView>(R.id.actorsTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_movie_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.movieDetailsFragmentLabel)
        setMovieDetails()
        setFavorieMovieCheckBox()
        setImdbClick()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setMovieDetails() {
        Glide.with(this)
            .load(args.posterUrl)
            .error(R.drawable.n_a)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(posterImageView as ImageView)
        titleTextView?.text = args.title
        imdbIdTextView?.text = args.imdbId
        releasedTextView?.text = args.released
        runtimeTextView?.text = args.runtime
        genreTextView?.text = args.genre
        directorTextView?.text = args.director
        writerTextView?.text = args.writer
        actorsTextView?.text = args.actors
    }

    private fun setFavorieMovieCheckBox() {
        (activity as? MoviesActivity)?.moviesViewModel?.movieWithimdbIdFromDatabase(args.imdbId)?.observe(viewLifecycleOwner, {
            // mark if is it a favorite movie
            favoriteCheckBox?.isChecked = !it.isNullOrEmpty()
            setFavoriteMovieCheckedChangeListener()
        })
    }

    private fun setFavoriteMovieCheckedChangeListener() = favoriteCheckBox?.setOnCheckedChangeListener { _, isChecked ->
        favoriteCheckBox?.isChecked = isChecked
        val viewModel = (activity as? MoviesActivity)?.moviesViewModel
        viewModel?.getMovieByImdbId(args.imdbId)?.observe(viewLifecycleOwner, {
            if (isChecked) {
                viewModel.insertMovieToDatabase(it)
            } else {
                viewModel.deleteMovieFromDatabase(it)
            }
        })
    }

    // clicking on imdbId value open the movie imdb web page
    private fun setImdbClick() {
        imdbIdTextView?.let { it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG }
        view?.findViewById<View>(R.id.imdbIdLayout)?.setOnClickListener {
            try {
                val builder = CustomTabsIntent.Builder()
                val customParams = CustomTabColorSchemeParams.Builder().setToolbarColor(Color.parseColor("#FF6200EE")).build()
                builder.setDefaultColorSchemeParams(customParams)
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse("https://www.imdb.com/title/${args.imdbId}"))
            } catch (e: IllegalStateException) {
                Log.w(MoviesActivity.TAG, "IllegalStateException - error=${e.message}")
            }
        }
    }
}