package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserData(
    @SerializedName("id") val userId: Int? = null,
    @SerializedName("name") var name: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("pictureUrl") var pictureUrl: String? = null,
    @SerializedName("credits") var credits: Int? = 0
)