package hu.bme.aut.netcar.data

import com.google.android.gms.maps.model.LatLng

data class Driver(
    val name: String,
    val location: LatLng,
    val picture: String?,
    val serial: String?,
    val carbrand: String,
    val carmodel: String,
    val freePlace: Int,
    val rating: Double
)