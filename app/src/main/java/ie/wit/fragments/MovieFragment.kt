package ie.wit.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ie.wit.R
import ie.wit.main.MovieApp
import ie.wit.models.MovieModel
import kotlinx.android.synthetic.main.fragment_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MovieFragment : Fragment() {

    lateinit var app: MovieApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MovieApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_movie, container, false)
        activity?.title = getString(R.string.action_movie)

        setButtonListener(root)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MovieFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener( layout: View) {
        layout.addMovieBtn.setOnClickListener {
            val dateformat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dateOfRelease = LocalDate.parse(movieReleaseDate.text, dateformat)

            app.moviesStore.create(
                MovieModel(title = movieTitle.text.toString(), director = movieDirector.text.toString(), releaseDate = dateOfRelease, earnings = movieEarnings.text.toString().toLong(),
                description = movieDescription.text.toString(), rating = movieRating.rating.toLong())
            )
        }
    }
}
