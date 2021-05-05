package ie.wit.models

interface MovieStore {
    fun findAll() : ArrayList<MovieModel>
    fun findById(id: Int) : MovieModel?
    fun create(movie: MovieModel)
    fun update(movie: MovieModel)
    fun deleteMovie(id: Int)
}