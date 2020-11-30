package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO (
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String
)