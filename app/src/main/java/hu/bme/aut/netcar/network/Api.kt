package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.CarData
import hu.bme.aut.netcar.data.UserData
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getAllUsers")
    fun getUsers() : Call<List<UserData>>

    @GET("getUser/{id}")
    fun getUserById(
        @Path("id") id: Int
    ) : Call<UserData>

    @GET("getCar/{id}")
    fun getCarById(
        @Path("id") id: Int
    ) : Call<CarData>

    @POST("addUser")
    fun addNewUser(
        @Body userData: UserData
    ) : Call<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    fun userLogin (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @PUT("updateUser/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body userData: UserData
    ) : Call<DefaultResponse>

    @FormUrlEncoded
    @PUT("/getUser/{id}/updateCar")
    fun updateCar(
        @Path("id") id: Int,
        @Field("brand") brand: String,
        @Field("model") model: String,
        @Field("serial") serial: String
    ) : Call<DefaultResponse>
}