package ie.wit.models

interface MovieStore {
    fun findAll() : List<MovieModel>
    fun findById(id: Long) : MovieModel?
    fun create(movie: MovieModel)
    fun update(movie: MovieModel)
}