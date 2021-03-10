package ie.wit.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ie.wit.helpers.exists
import ie.wit.helpers.read
import ie.wit.helpers.write
import java.util.*
import kotlin.collections.ArrayList


val JSON_FILE = "movies.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<MovieModel>>() {}.type

fun generateRandomId(): Long {
  return Random().nextLong()
}

class MovieJSONStore : MovieStore {

  val context: Context
  var movies = ArrayList<MovieModel>()

  constructor(context: Context){
    this.context = context
    if(exists(context, JSON_FILE)) {
      deserialize()
    }
  }

  override fun findAll(): ArrayList<MovieModel> {
    return movies
  }

  override fun findById(id: Long): MovieModel? {
    val foundMovie: MovieModel? = movies.find { it.id == id }
    return foundMovie
  }

  override fun create(movie: MovieModel) {
    movie.id = generateRandomId()
    movies.add(movie)
    serialize()
  }

  override fun update(movie: MovieModel) {
    var foundMovie: MovieModel? = movies.find { m -> m.id == movie.id }
    if (foundMovie != null) {
      foundMovie.title = movie.title
      foundMovie.director = movie.director
      foundMovie.releaseDate = movie.releaseDate
      foundMovie.earnings = movie.earnings
      foundMovie.description = movie.description
      foundMovie.rating = movie.rating
      foundMovie.image = movie.image
    }

    serialize()
  }

  override fun deleteMovie(id: Long) {
    movies.removeIf{m-> (id == m.id)}
    serialize()
  }

  private fun serialize() {
    val jsonString = gsonBuilder.toJson(movies,
        listType
    )
    write(context, JSON_FILE, jsonString)
  }

  private fun deserialize() {
    val jsonString = read(context,
        JSON_FILE
    )
    movies = Gson().fromJson(jsonString,
        listType
    )
  }
}