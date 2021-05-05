package ie.wit.fragments


import android.app.Activity
import android.content.Intent
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
import org.jetbrains.anko.toast
import ie.wit.R
import ie.wit.activities.Home
import ie.wit.helpers.readImage
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
    val dateformat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var edit = false

    var movie = MovieModel()

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

        var args = getArguments()
        Log.i("1", args.toString())

        if (args != null) {

            if (!args.isEmpty) {
                var editMovieID = args.getInt("movieID")
                Log.i("2", editMovieID.toString())
                //updateMovie(root,editMovieID)
            }
        }

        if (root.movieImage.drawable == null) {
            root.addImageBtn.setText(R.string.image_add_button)
        }
        else{
            root.addImageBtn.setText(R.string.change_image)
        }

        setButtonListener(root)
        return root
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

            writeNewMovie(MovieModel(title = movieTitle.text.toString(), director = movieDirector.text.toString(), releaseDate = movieReleaseDate.text.toString(),
            earnings = movieEarnings.text.toString().toInt(),description = movieDescription.text.toString(),rating = movieRating.rating.toInt()))


//            val dateOfRelease = movieReleaseDate.text.toString()
//
//            if (CheckValidDate(dateOfRelease)) {
//
//                movie.title = movieTitle.text.toString()
//                movie.director = movieDirector.text.toString()
//                movie.releaseDate = LocalDate.parse(dateOfRelease, dateformat)
//                movie.earnings = movieEarnings.text.toString().toLong()
//                movie.description = movieDescription.text.toString()
//                movie.rating = movieRating.rating.toLong()
//
//                if (movie.title.isEmpty() || movie.director.isEmpty() || movie.description.isEmpty()) {
//                    activity?.toast("Please enter required fields (title,director,description)")
//                } else {
//                    if (edit) {
//                        app.moviesStore.update(movie.copy())
//                        activity?.toast("Successfully updated movie details")
//                        (activity as Home).navigateTo(ViewFragment())
//                    } else {
//                        app.moviesStore.create(movie.copy())
//                        activity?.toast("Movie has been added successfully")
//                        Log.i("create", movie.releaseDate.toString())
//
//                        (activity as Home).navigateTo(ViewFragment())
//                    }
//                }
//            } else {
//                activity?.toast("Please enter a valid date")
//            }
        }
        layout.addImageBtn.setOnClickListener {
            pickImageFromGallery()
        }
    }

    fun updateMovie(view: View, editMovieID: Long){
//        var movieForEdit = app.moviesStore.findById(editMovieID)
//
//        if (movieForEdit != null) {
//
//            Log.i("3", movieForEdit.title)
//            movie = movieForEdit
//            edit = true
//            view.movieTitle.setText(movie.title)
//            view.movieDirector.setText(movie.director)
//            view.movieReleaseDate.setText(movie.releaseDate.format(dateformat))
//            view.movieEarnings.setText(movie.earnings.toString())
//            view.movieDescription.setText(movie.description)
//            view.movieRating.rating = movie.rating.toFloat()
//            view.movieImage.setImageBitmap(readImageFromPath((activity as Home), movie.image))
//
//            view.addMovieBtn.setText(R.string.save_changes)
//        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun CheckValidDate(date: String) : Boolean{
        var isValid: Boolean

        isValid = try{
           dateformat.parse(date)
            true
        } catch(ex: DateTimeParseException){
            ex.printStackTrace()
            false
        }
        return isValid
    }

    fun writeNewMovie(movie: MovieModel) {
        // Create new donation at /donations & /donations/$uid
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data !=null) {


            when (requestCode) {
                IMAGE_PICK_CODE -> {
                        movie.image = data.getData().toString()
                        addImageBtn.setText(R.string.change_image)

                    val content_uri = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, content_uri)
                    movieImage.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

