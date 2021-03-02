package ie.wit.main

import android.app.Application
import android.util.Log
import ie.wit.models.MovieMemStore
import ie.wit.models.MovieStore

class MovieApp : Application() {

    lateinit var moviesStore: MovieStore

    override fun onCreate() {
        super.onCreate()
        moviesStore = MovieMemStore()
        Log.v("Movie","Movie App Starting")
    }

}