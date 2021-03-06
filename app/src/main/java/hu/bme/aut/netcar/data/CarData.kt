package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CarData(
    @SerializedName("id") val carId: Int? = null,
    @SerializedName("user") var userData: UserData?,
    @SerializedName("brand") var brand: String?,
    @SerializedName("model") var model: String?,
    @SerializedName("serial") var serial: String?,
    @SerializedName("pic") var pic: String?,
    @SerializedName("hasBoot") var hasBoot: Boolean?,
    @SerializedName("seats") var seats: Int?,
    @SerializedName("placeInBoot") var placeInBoot: Int?,
    @SerializedName("freeSeats") var freeSeats: Int? = null,
    @SerializedName("freePlace") var freePlace: Int? = null
)