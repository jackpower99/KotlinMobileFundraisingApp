package ie.wit.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Movie
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.isSrgb
import androidx.lifecycle.ReportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.toast
import ie.wit.R
import ie.wit.activities.Home
import ie.wit.fragments.MovieFragment.Companion.newInstance
import ie.wit.fragments.ViewFragment.Companion.newInstance
//import ie.wit.helpers.readImage
import ie.wit.helpers.readImageFromPath
import ie.wit.helpers.showImagePicker
import ie.wit.main.MovieApp
import ie.wit.models.MovieModel
import ie.wit.utils.createLoader
import ie.wit.utils.hideLoader
import ie.wit.utils.showLoader
import kotlinx.android.synthetic.main.fragment_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.ParseException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MovieFragment : Fragment(), AnkoLogger {

    lateinit var app: MovieApp
    lateinit var loader : AlertDialog
    var edit = false
    var movie : MovieModel? = null

    var favourite = false

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
        loader = createLoader(activity!!)
        info(edit)

        var args = getArguments()
        info(args.toString())

        if (args != null) {
            if (!args.isEmpty) {
                edit = true
                movie = args.getParcelable("movieID")
                info(movie?.uid)
            }
        }
        if(movie != null) {
            updateMovieFields(root, movie!!)
        }
        if (root.movieImage.drawable == null) {
            root.addImageBtn.setText(R.string.image_add_button)
        } else {
            root.addImageBtn.setText(R.string.change_image)
        }
        setButtonListener(root)
        setFavouriteListener(root)
        return root
    }

    fun updateMovie(uid: String?, movie: MovieModel) {
        info(movie.title)
        info(movie.uid)
        app.database.child("movies").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(movie)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Movie error : ${error.message}")
                    }
                })
    }

    fun updateUserMovie(userId: String, uid: String?, movie: MovieModel) {

        app.database.child("user-movies").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(movie)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, ViewFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Movie error : ${error.message}")
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MovieFragment().apply {
                arguments = Bundle().apply {}
            }

        private val IMAGE_PICK_CODE = 1000;
    }

    fun setButtonListener( layout: View) {

        layout.addMovieBtn.setOnClickListener {

            info(edit.toString())

            if(edit) {
                movie!!.title = movieTitle.text.toString()
                movie!!.director = movieDirector.text.toString()
                movie!!.releaseDate = movieReleaseDate.text.toString()
                movie!!.earnings = movieEarnings.text.toString().toInt()
                movie!!.description = movieDescription.text.toString()
                movie!!.rating = movieRating.rating.toInt()
            }

                if (movieTitle.text.toString() == "" || movieDirector.text.toString() == "" || movieDescription.text.toString() == "") {
                    activity?.toast("Please enter required fields (title,director,description)")
                } else {
                    if (edit) {
                        updateMovie(movie!!.uid,MovieModel(title = movieTitle.text.toString(), director = movieDirector.text.toString(), releaseDate = movieReleaseDate.text.toString(),
                            earnings = movieEarnings.text.toString().toInt(),description = movieDescription.text.toString(),rating = movieRating.rating.toInt(),isFavourite = favourite))
                        updateUserMovie(app.auth.currentUser!!.uid,movie!!.uid,MovieModel(title = movieTitle.text.toString(), director = movieDirector.text.toString(), releaseDate = movieReleaseDate.text.toString(),
                            earnings = movieEarnings.text.toString().toInt(),description = movieDescription.text.toString(),rating = movieRating.rating.toInt(), isFavourite = favourite))
                    }
                     else {
                        writeNewMovie(MovieModel(title = movieTitle.text.toString(), director = movieDirector.text.toString(), isFavourite = favourite, releaseDate = movieReleaseDate.text.toString(),
                            earnings = movieEarnings.text.toString().toInt(),description = movieDescription.text.toString(),rating = movieRating.rating.toInt(),profilepic = app.userImage.toString()))

                        (activity as Home).navigateTo(ViewFragment())
                    }
                }
        }
        layout.addImageBtn.setOnClickListener {
            pickImageFromGallery()
        }
    }

    fun updateMovieFields(view: View, movie : MovieModel){

            view.movieTitle.setText(movie.title)
            view.movieDirector.setText(movie.director)
            view.movieReleaseDate.setText(movie.releaseDate)
            view.movieEarnings.setText(movie.earnings.toString())
            view.movieDescription.setText(movie.description)
            view.movieRating.rating = movie.rating.toFloat()
           // view.movieImage.setImageBitmap(readImageFromPath((activity as Home), movie.image))

            view.addMovieBtn.setText(R.string.save_changes)
      //  }
    }


    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

//    fun CheckValidDate(date: String) : Boolean{
//        var isValid: Boolean
//
//        isValid = try{
//           dateformat.parse(date)
//            true
//        } catch(ex: DateTimeParseException){
//            ex.printStackTrace()
//            false
//        }
//        return isValid
//    }

    fun setFavouriteListener (layout: View) {
        layout.imagefavourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!favourite) {
                    info(favourite.toString())
                    layout.imagefavourite.setImageResource(android.R.drawable.star_big_on)
                    favourite = true
                }
                else {
                    layout.imagefavourite.setImageResource(android.R.drawable.star_big_off)
                    favourite = false
                }
            }
        })
    }

    fun writeNewMovie(movie: MovieModel) {
        showLoader(loader, "Adding Movie to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        info(uid)
        val key = app.database.child("movies").push().key
        info(key)
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        movie.uid = key
        info(movie.uid)
        val movieValues = movie.toMap()

        val childUpdates = HashMap<String, Any>()
        info(childUpdates)
        childUpdates["/movies/$key"] = movieValues
        childUpdates["/user-movies/$uid/$key"] = movieValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && data !=null) {
//
//
//            when (requestCode) {
//                IMAGE_PICK_CODE -> {
//                        movie?.image = data.getData().toString()
//                        addImageBtn.setText(R.string.change_image)
//
//                    val content_uri = data.data
//                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, content_uri)
//                    movieImage.setImageBitmap(bitmap)
//                    }
//                }
//            }
//        }
    }

