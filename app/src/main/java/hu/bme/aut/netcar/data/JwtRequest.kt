package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JwtRequest(
    @SerializedName("serialVersionUID") var serialVersionUID: Long = 5926468583005150707L,
    @SerializedName("username") var username: String?,
    @SerializedName("password") var password: String?
)