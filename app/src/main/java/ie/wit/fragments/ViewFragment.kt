package ie.wit.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import ie.wit.R
import ie.wit.activities.Home
import ie.wit.adapters.MovieClickListener
import ie.wit.adapters.MoviesAdapter
import ie.wit.main.MovieApp
import ie.wit.models.MovieModel
import ie.wit.utils.createLoader
import ie.wit.utils.showLoader
import kotlinx.android.synthetic.main.fragment_movies_view.view.*
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.utils.hideLoader
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

open class ViewFragment : Fragment(), MovieClickListener, AnkoLogger {

    lateinit var app: MovieApp
    private lateinit var editTextSearch: EditText

    lateinit var loader : AlertDialog

    lateinit var removedMovie : MovieModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MovieApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_movies_view, container, false)

        setSwipeRefresh(root)
        loader = createLoader(activity!!)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))

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

        //searchMovies(root)

        return root
    }

    private fun showDialog(viewHolder: RecyclerView.ViewHolder, root: View){
        val builder = AlertDialog.Builder(activity as Home)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this movie?")

        builder.setPositiveButton("Confirm"){ dialog, which ->
            val position = viewHolder.adapterPosition
            val uid = (viewHolder.itemView.tag as MovieModel).uid
            info(uid)
            //val movieTest = app.movies
//
 //           removedMovie = movieTest[position]

            //info(removedMovie.uid)
            deleteMovie(uid)
            info("1")
            deleteUserMovie(app.auth.currentUser!!.uid, uid)

            root.recyclerView.adapter?.notifyItemRemoved(position)
            root.recyclerView.adapter?.notifyDataSetChanged()
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

    fun deleteUserMovie(userId: String, uid: String?) {
        info(app.database.child("user-movies").child(userId).child(uid!!))
        app.database.child("user-movies").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Movie error : ${error.message}")
                    }
                })
    }

    fun deleteMovie(uid: String?) {
        app.database.child("movies").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Movie error : ${error.message}")
                    }
                })
    }

    override fun onMovieClick(movie: MovieModel) {
        Log.i("local", movie.uid.toString())
                val fragment = MovieFragment()
                val args = Bundle()
                args.putParcelable("movieID", movie)
                fragment.setArguments(args)

        (activity as Home).navigateTo(fragment)
    }

//    fun searchMovies(view: View){
//        editTextSearch = view.search_bar
//
//        editTextSearch.addTextChangedListener(object :TextWatcher{
//            override fun afterTextChanged(s: Editable?) {
//                filterList(s.toString(),view)
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//        })

 //   }
    override fun onResume() {
        super.onResume()
        if(this::class == ViewFragment::class)
        getAllMovies(app.auth.currentUser!!.uid, this.view!!)
    }

    fun getAllMovies(userId: String?, root: View) {
        showLoader(loader, "Downloading movies from Firebase")
        val moviesList = ArrayList<MovieModel>()
        app.database.child("user-movies").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Movie error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot!!.children
                    children.forEach {
                        val movie = it.getValue<MovieModel>(MovieModel::class.java!!)
                        info(movie)
                        moviesList.add(movie!!)
                        info(moviesList)
                        root.recyclerView?.adapter =
                            MoviesAdapter(moviesList,false, this@ViewFragment)
                        root.recyclerView?.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh(root)
                        hideLoader(loader)
                        app.database.child("user-movies").child(userId).removeEventListener(this)
                    }
                }
            })
    }
    open fun setSwipeRefresh(root : View) {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllMovies(app.auth.currentUser!!.uid, root)
            }
        })
    }

    fun checkSwipeRefresh(root: View) {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

//    private fun filterList(filterItem: String,root: View){
//        var tempList: ArrayList<MovieModel> = ArrayList()
//
//        for(m in app.moviesStore.findAll()){
//            if(filterItem in m.title){
//                tempList.add(m)
//            }
//        }
//        (root.recyclerView.adapter as MoviesAdapter).updateList(tempList)
//        (root.recyclerView.adapter as MoviesAdapter).notifyDataSetChanged()
//    }
}
