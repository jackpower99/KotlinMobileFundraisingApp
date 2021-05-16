package ie.wit.main

import android.app.Application
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import ie.wit.models.MovieJSONStore
import ie.wit.models.MovieMemStore
import ie.wit.models.MovieModel
import ie.wit.models.MovieStore

class MovieApp : Application() {

    //lateinit var moviesStore: MovieStore
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var storage: StorageReference

    lateinit var userImage : Uri

    //var movies : ArrayList<MovieModel> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        //moviesStore = MovieJSONStore(applicationContext)
        Log.v("Movie","Movie App Starting")
    }

}