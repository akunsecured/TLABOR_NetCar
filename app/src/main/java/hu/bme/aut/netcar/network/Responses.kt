package hu.bme.aut.netcar.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefaultResponse (
    val message: String
)