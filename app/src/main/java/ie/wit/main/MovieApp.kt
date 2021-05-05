package ie.wit.main

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import ie.wit.models.MovieJSONStore
import ie.wit.models.MovieMemStore
import ie.wit.models.MovieModel
import ie.wit.models.MovieStore

class MovieApp : Application() {

    lateinit var moviesStore: MovieStore
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    var movies : ArrayList<MovieModel> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        moviesStore = MovieJSONStore(applicationContext)
        Log.v("Movie","Movie App Starting")
    }

}