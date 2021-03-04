package ie.wit.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ie.wit.R
import ie.wit.fragments.MovieFragment
import ie.wit.fragments.ViewFragment
import ie.wit.helpers.readImageFromPath
import ie.wit.models.MovieModel
import kotlinx.android.synthetic.main.card_movie.view.*


interface MovieClickListener {
    fun onMovieClick(movie: MovieModel)
}

class MoviesAdapter constructor(private var movies: List<MovieModel>, private val listener: MovieClickListener)
    : RecyclerView.Adapter<MoviesAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val movie = movies[holder.adapterPosition]
        holder.bind(movie, listener)
    }

    override fun getItemCount(): Int = movies.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(movie: MovieModel, listener: MovieClickListener) {
            itemView.movie_view_Title.text = movie.title
            itemView.movie_view_director.text = movie.director
            itemView.movie_view_releaseDate.text = movie.releaseDate.toString()
            itemView.movie_view_ratingBar.rating = movie.rating.toFloat()
            itemView.movie_view_Image.setImageBitmap(readImageFromPath(itemView.context,movie.image))
            itemView.setOnClickListener{listener.onMovieClick(movie)}

//            itemView.setOnClickListener {

//                Log.i("test", "Item clicked")
//                val fragment = MovieFragment()
//                val args = Bundle()
//                args.putLong("movieID", movie.id)
//                fragment.setArguments(args)
//
//                val activity = itemView.context as AppCompatActivity
//                activity.supportFragmentManager.beginTransaction().replace(R.id.homeFrame, fragment)
//                    .addToBackStack(null).commit()
//            }

            }
        }
}