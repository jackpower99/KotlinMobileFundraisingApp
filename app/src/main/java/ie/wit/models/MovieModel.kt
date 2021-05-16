package ie.wit.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class MovieModel (var uid: String? = "", var id: Int = 0, var title: String = "",
                       var director: String = "",
                       var releaseDate: String = "",
                       var earnings : Int = 0,
                       var description: String = "",
                       var rating : Int = 0,
                       var profilepic : String ="",
                       var isFavourite : Boolean = true
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "id" to id,
            "title" to title,
            "director" to director,
            "releaseDate" to releaseDate,
            "earnings" to earnings,
            "description" to description,
            "rating" to rating,
            "profilepic" to profilepic,
            "isFavourite" to isFavourite
        )
    }
}