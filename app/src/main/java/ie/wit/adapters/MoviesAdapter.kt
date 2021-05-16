package ie.wit.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.R
import ie.wit.helpers.readImageFromPath
import ie.wit.models.MovieModel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.view.*


interface MovieClickListener {
    fun onMovieClick(movie: MovieModel)
}

class MoviesAdapter constructor(private var movies: ArrayList<MovieModel>,reportall: Boolean, private val listener: MovieClickListener)
    : RecyclerView.Adapter<MoviesAdapter.MainHolder>() {

    val reportAll = reportall

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
        Log.i("isFav", movie.isFavourite.toString())
        holder.bind(movie, listener,reportAll)
    }

    override fun getItemCount(): Int = movies.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            movie: MovieModel,
            listener: MovieClickListener,
            reportAll: Boolean
        ) {
            itemView.tag = movie
            itemView.movie_view_Title.text = movie.title
            itemView.movie_view_director.text = movie.director
            itemView.movie_view_releaseDate.text = movie.releaseDate.toString()
            itemView.movie_view_ratingBar.rating = movie.rating.toFloat()
            Log.i("1",movie.isFavourite.toString())
            if(movie.isFavourite){
                itemView.starImageCard.setImageResource(android.R.drawable.star_big_on)
            }
//            itemView.movie_view_Image.setImageBitmap(
//                readImageFromPath(
//                    itemView.context,
//                    movie.image
//                )
//            )
            itemView.setOnClickListener { listener.onMovieClick(movie) }

            if (!reportAll)
                itemView.setOnClickListener {
                    listener.onMovieClick(movie)
                }
            if(!movie.profilepic.isEmpty()) {
                Picasso.get().load(movie.profilepic.toUri())
                    //.resize(180, 180)
                    .transform(CropCircleTransformation())
                    .into(itemView.userpic)
            }
            else
                itemView.userpic.setImageResource(R.mipmap.ic_launcher)
        }
    }
//    fun updateList(list: ArrayList<MovieModel>) {
//        movies = list
//        notifyDataSetChanged()
//    }

}