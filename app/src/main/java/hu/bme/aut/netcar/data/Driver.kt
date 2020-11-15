package hu.bme.aut.netcar.data

import com.google.android.gms.maps.model.LatLng

data class Driver(
    val name: String,
    val carbrand: String,
    val carmodel: String,
    val serial: String,
    val seats: Int,
    val location: LatLng
)