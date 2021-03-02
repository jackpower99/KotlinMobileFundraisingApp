package ie.wit.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class MovieModel (var id: Long = 0, val title: String = "",
                       val director: String = "",
                       val releaseDate: LocalDate,
                       val earnings : Long,
                       val description: String = "",
                       val rating : Long = 0
) : Parcelable