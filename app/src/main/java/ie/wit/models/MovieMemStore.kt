package ie.wit.models

import android.util.Log

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class MovieMemStore : MovieStore {

    val movies = ArrayList<MovieModel>()


    override fun findAll(): List<MovieModel> {
        return movies
    }

    override fun findById(id: Long): MovieModel? {
        val foundMovie: MovieModel? = movies.find { it.id == id }
        return foundMovie
    }

    override fun create(movie: MovieModel) {
        movie.id = getId()
        movies.add(movie)
        logAll()
    }

    fun logAll() {
        movies.forEach { Log.v("Movie", "${it}") }
    }
}