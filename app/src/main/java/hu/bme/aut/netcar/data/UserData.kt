package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class UserData(
    @SerializedName("id") val userId: Int? = null,
    @SerializedName("username") var username: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("credits") var credits: Int? = 0,
    @SerializedName("valid") var valid: Boolean = false,
    @SerializedName("visible") var visible: Boolean = false,
    @SerializedName("isInProgress") var isInProgress: Boolean = false,
    @SerializedName("location") var location: Coord,
    @SerializedName("ratings") var ratings: ArrayList<Int> = ArrayList()
) : Serializable