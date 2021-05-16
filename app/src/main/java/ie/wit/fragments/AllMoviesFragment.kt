package ie.wit.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.wit.R

import ie.wit.adapters.MovieClickListener
import ie.wit.adapters.MoviesAdapter

import ie.wit.models.MovieModel
import ie.wit.utils.*
import kotlinx.android.synthetic.main.fragment_movies_view.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class AllMoviesFragment : ViewFragment(),
    MovieClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_movies_view, container, false)
        activity?.title = getString(R.string.menu_movie_all)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh(root)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AllMoviesFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun setSwipeRefresh(root: View) {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllUsersMovies(root)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        view?.let { getAllUsersMovies(it) }
    }

    fun getAllUsersMovies(root: View) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading All Users Movies from Firebase")
        val moviesList = ArrayList<MovieModel>()
        app.database.child("movies")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Movie error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val movie = it.
                        getValue<MovieModel>(MovieModel::class.java)

                        moviesList.add(movie!!)
                        root.recyclerView.adapter =
                            MoviesAdapter(moviesList,true, this@AllMoviesFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh(root)

                        app.database.child("movies").removeEventListener(this)
                    }
                }
            })
    }
}