package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.CarData
import hu.bme.aut.netcar.data.JwtRequest
import hu.bme.aut.netcar.data.UserDTO
import hu.bme.aut.netcar.data.UserData
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getAllUsers")
    fun getUsers() : Call<List<UserData>>

    @GET("getUser/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ) : UserData

    @GET("getCar/{id}")
    suspend fun getCarById(
        @Path("id") id: Int
    ) : CarData

    @POST("register")
    suspend fun register(
        @Body user: UserDTO
    ) : DefaultResponse

    @POST("login")
    suspend fun userLogin(
        @Body authenticationRequest: JwtRequest
    ) : DefaultResponse

    @PUT("updateUser/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body userData: UserData
    ) : DefaultResponse

    @FormUrlEncoded
    @PUT("/getUser/{id}/updateCar")
    suspend fun updateCar(
        @Path("id") id: Int,
        @Field("brand") brand: String,
        @Field("model") model: String,
        @Field("serial") serial: String,
        @Field("pic") pic: String,
        @Field("hasBoot") hasBoot: Boolean,
        @Field("seats") seats: Int,
        @Field("placeInBoot") placeInBoot: Int
    ) : DefaultResponse
}