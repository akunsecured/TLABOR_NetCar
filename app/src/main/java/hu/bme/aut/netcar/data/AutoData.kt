package hu.bme.aut.netcar.data

import android.graphics.Bitmap
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AutoData(
    val model: String?,
    val brand: String?,
    val freePlaces: Int?,
    val plateNumber: String?,
    val carImage: Bitmap?
)