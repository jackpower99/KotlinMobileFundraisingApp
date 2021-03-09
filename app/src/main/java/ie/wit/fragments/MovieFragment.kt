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
import kotlinx.android.synthetic.main.fragment_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieFragment : Fragment() {

    lateinit var app: MovieApp
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

        var args = getArguments()
        Log.i("1", args.toString())

        if (args != null) {

            if (!args.isEmpty) {
                var editMovieID = args.getLong("movieID")
                Log.i("2", editMovieID.toString())
                updateMovie(root,editMovieID)
            }
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

            val dateOfRelease = LocalDate.parse(movieReleaseDate.text, dateformat)

            movie.title = movieTitle.text.toString()
            movie.director = movieDirector.text.toString()
            movie.releaseDate = dateOfRelease
            movie.earnings = movieEarnings.text.toString().toLong()
            movie.description = movieDescription.text.toString()
            movie.rating = movieRating.rating.toLong()

            if(movie.title.isEmpty()||movie.director.isEmpty()||movie.description.isEmpty()||movieReleaseDate.length()==0) {
                activity?.toast("Please enter required fields (title,director,description,release date")
            }
            else {
                if(edit){
                    app.moviesStore.update(movie.copy())
                } else {
                    app.moviesStore.create(movie.copy())
                }
            }
        }
        layout.addImageBtn.setOnClickListener{
            pickImageFromGallery()
        }
    }

    fun updateMovie(view: View, editMovieID: Long){
        var movieForEdit = app.moviesStore.findById(editMovieID)

        if (movieForEdit != null) {

            Log.i("3", movieForEdit.title)
            movie = movieForEdit
            edit = true
            view.movieTitle.setText(movie.title)
            view.movieDirector.setText(movie.director)
            view.movieReleaseDate.setText(movie.releaseDate.format(dateformat))
            view.movieEarnings.setText(movie.earnings.toString())
            view.movieDescription.setText(movie.description)
            view.movieRating.rating = movie.rating.toFloat()
            view.movieImage.setImageBitmap(readImageFromPath((activity as Home), movie.image))

            if (movie.image != null) {
                view.addImageBtn.setText(R.string.change_image)
            }
            view.addMovieBtn.setText(R.string.save_changes)
        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
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

