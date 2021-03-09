package ie.wit.fragments


import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import ie.wit.R
import ie.wit.activities.Home
import ie.wit.adapters.MovieClickListener
import ie.wit.adapters.MoviesAdapter
import ie.wit.main.MovieApp
import ie.wit.models.MovieMemStore
import ie.wit.models.MovieModel
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*

class ViewFragment : Fragment(), MovieClickListener {

    lateinit var app: MovieApp
    private lateinit var removedMovie: MovieModel

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

        val itemSwipe = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
               showDialog(viewHolder,root)
            }
        }

        val swap = ItemTouchHelper(itemSwipe)
        swap.attachToRecyclerView(root.recyclerView)

        return root
    }

    private fun showDialog(viewHolder: RecyclerView.ViewHolder, root: View){
        val builder = AlertDialog.Builder(activity as Home)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this movie?")

        builder.setPositiveButton("Confirm"){ dialog, which ->
            val position = viewHolder.adapterPosition
          //  removedMovie = MovieMemStore().movies[position]
            app.moviesStore.deleteMovie(position.toLong())
            root.recyclerView.adapter?.notifyItemRemoved(position)
            //Log.i("removed movie:", removedMovie.title)

        }

        builder.setNegativeButton("Cancel"){ dialog, which ->
            val position = viewHolder.adapterPosition
            root.recyclerView.adapter?.notifyItemChanged(position)
        }
        builder.show()
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
