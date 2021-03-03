package ie.wit.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class MovieModel (var id: Long = 0, var title: String = "",
                       var director: String = "",
                       var releaseDate: LocalDate = LocalDate.now(),
                       var earnings : Long = 0,
                       var description: String = "",
                       var rating : Long = 0,
                       var image : String =""
) : Parcelable