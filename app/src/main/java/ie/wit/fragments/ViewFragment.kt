package ie.wit.fragments


import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import ie.wit.R
import ie.wit.activities.Home
import ie.wit.adapters.MovieClickListener
import ie.wit.adapters.MoviesAdapter
import ie.wit.main.MovieApp
import ie.wit.models.MovieModel
import kotlinx.android.synthetic.main.fragment_report.view.*

class ViewFragment : Fragment(), MovieClickListener {

    lateinit var app: MovieApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MovieApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_report, container, false)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        root.recyclerView.adapter = MoviesAdapter(app.moviesStore.findAll(),this)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ViewFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onMovieClick(movie: MovieModel) {
        Log.i("test", movie.id.toString())
                val fragment = MovieFragment()
                val args = Bundle()
                args.putLong("movieID", movie.id)
                fragment.setArguments(args)

        (activity as Home).navigateTo(fragment)
    }
}
