package ie.wit.models

import android.content.Context
import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import ie.wit.helpers.exists
import ie.wit.helpers.read
import ie.wit.helpers.write
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime.ofInstant
import java.time.OffsetDateTime.ofInstant
import java.time.OffsetTime.ofInstant
import java.time.ZoneId
import java.time.ZonedDateTime.ofInstant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log10


val JSON_FILE = "movies.json"
val gsonBuilder = GsonBuilder()
  .setPrettyPrinting()
  //.registerTypeAdapter(LocalDate::class.java, JsonSerialCustom())
 // .registerTypeAdapter(LocalDate::class.java, JsonDeserialCustom())
  .create()
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
    for(m in movies) {
      Log.i("deserialize", m.releaseDate.toString())
    }
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
    Log.i("serialize", jsonString)
    write(context, JSON_FILE, jsonString)
  }

  private fun deserialize() {
    val jsonString = read(context,
        JSON_FILE
    )
    movies = Gson().fromJson(jsonString,
        listType)

    for(m in movies){
      Log.i("deserialize", m.releaseDate.toString())
    }
  }
}

//class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {
//  override fun write(out: JsonWriter, value: LocalDate?) {
//    out.value(DateTimeFormatter.ISO_LOCAL_DATE.format(value))
//  }
//
//  override fun read(input: JsonReader): LocalDate = LocalDate.parse(input.nextString())
//
//}

//class JsonSerialCustom : JsonSerializer<LocalDate>{
//
//  val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//
//  override fun serialize(
//    src: LocalDate?,
//    typeOfSrc: Type?,
//    context: JsonSerializationContext?
//  ): JsonElement {
//    return JsonPrimitive(formatter.format(src))
//  }
//
//}

//class JsonDeserialCustom : JsonDeserializer<LocalDate>{
//  override fun deserialize(
//    json: JsonElement?,
//    typeOfT: Type?,
//    context: JsonDeserializationContext?
//  ): LocalDate {
//    return LocalDate.parse(json?.asString, DateTimeFormatter.ofPattern("dd-MM-yyyy").withLocale(Locale.ENGLISH))
//  }
//
//}