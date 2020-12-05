package com.app.movies.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.movies.R
import com.app.movies.data.Movie
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MoviesRecyclerViewAdapter : RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder>() {

    class MovieViewHolder(val movieView : View) : RecyclerView.ViewHolder(movieView) {
        val posterImageView: ImageView by lazy { movieView.findViewById(R.id.posterImageView) }
        val titleTextView: TextView by lazy { movieView.findViewById(R.id.titleTextView) }
        val yearTetView: TextView by lazy { movieView.findViewById(R.id.yearTextView) }
        val favoriteCheckBox: CheckBox by lazy { movieView.findViewById(R.id.favoriteCheckBox) }
    }

    var movies: List<Movie> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var movieItemClicked: (imdbId: String) -> Unit = {}
    var movieFavoriteClicked: (Movie, Boolean) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val movieItemView =  LayoutInflater.from(parent.context).inflate(R.layout.movie_item_layout, parent, false)
        return MovieViewHolder(movieItemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.titleTextView.text = movies[position].title
        holder.yearTetView.text = movies[position].year
        setMoviePoster(movies[position].poster, holder.posterImageView)
        setFavorieMovieCheckBox(holder.favoriteCheckBox, position)
        setMovieItemClickListener(holder.movieView, position)
    }

    override fun getItemCount() = movies.size

    private fun setMoviePoster(posterUrl: String, imageView: ImageView) = Glide
            .with(imageView.context)
            .load(posterUrl)
            .error(R.drawable.n_a)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

    private fun setFavorieMovieCheckBox(favoriteCheckBox: CheckBox, position: Int) {
        val moviesActivity = favoriteCheckBox.context as? MoviesActivity
        moviesActivity?.moviesViewModel?.movieWithimdbIdFromDatabase(movies[position].imdbID)?.observe(moviesActivity, {
            // mark if is it a favorite movie
            favoriteCheckBox.setOnCheckedChangeListener(null) // set listener to null before mark favorite to not get callback
            favoriteCheckBox.isChecked = !it.isNullOrEmpty()
            setFavoriteMovieCheckedChangeListener(favoriteCheckBox, position)
        })
        setFavoriteMovieCheckedChangeListener(favoriteCheckBox, position)
    }

    private fun setFavoriteMovieCheckedChangeListener(favoriteCheckBox: CheckBox, position: Int) = favoriteCheckBox.setOnCheckedChangeListener { _, isChecked ->
        movieFavoriteClicked(movies[position], isChecked)
    }

    private fun setMovieItemClickListener(movieItemView: View, position: Int) = movieItemView.setOnClickListener {
        movieItemClicked(movies[position].imdbID)
    }
}