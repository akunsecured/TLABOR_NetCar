package hu.bme.aut.netcar.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ServiceRequest(
    @SerializedName("srid") val srid: Int? = null,
    @SerializedName("driverID") var driverID: Int? = null,
    @SerializedName("passengerID") var passengerID: Int? = null,
    @SerializedName("payment") var payment: Int? = null,
    @SerializedName("destinationPos") var destinationPos: Coord? = null,
    @SerializedName("startTime") var startTime: String? = null,
    @SerializedName("finishTime") var finishTime: String? = null,
    @SerializedName("sRstatus") var sRstatus: SRstatus? = SRstatus.PENDING
)

enum class SRstatus {
    PENDING, DENIED, INPROGRESS, FINISHED
}

@JsonClass(generateAdapter = true)
data class Coord(
    @SerializedName("x") var x: Double?,
    @SerializedName("y") var y: Double?
)