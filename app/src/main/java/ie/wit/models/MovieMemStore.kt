package ie.wit.models

import android.util.Log
import kotlinx.android.synthetic.main.fragment_movie.*

var lastId = 0

internal fun getId(): Int {
    return lastId++
}

class MovieMemStore : MovieStore {

    val movies = ArrayList<MovieModel>()


    override fun findAll(): ArrayList<MovieModel> {
        return movies

    }

    override fun findById(id: Int): MovieModel? {
        val foundMovie: MovieModel? = movies.find { it.id == id }
        return foundMovie
    }

    override fun create(movie: MovieModel) {
        movie.id = getId()
        movies.add(movie)
        logAll()
    }

    override fun update(movie: MovieModel) {
        var foundMovie: MovieModel? = movies.find { m -> m.id == movie.id}
        if (foundMovie != null) {
            foundMovie.title = movie.title
            foundMovie.director = movie.director
            foundMovie.releaseDate = movie.releaseDate
            foundMovie.earnings = movie.earnings
            foundMovie.description = movie.description
            foundMovie.rating = movie.rating
            foundMovie.image = movie.image
            logAll()
        }
    }

    override fun deleteMovie(id: Int) {
        movies.removeIf{m-> (id == m.id)}
        Log.i("deleted movie: ", id.toString())

    }

    fun logAll() {
        movies.forEach { Log.v("Movie", "${it}") }
    }
}