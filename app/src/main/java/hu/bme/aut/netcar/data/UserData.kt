package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserData(
    @SerializedName("id") val userId: Int? = null,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("pictureUrl") val pictureUrl: String? = null,
    @SerializedName("credits") val credits: Int? = 0
)