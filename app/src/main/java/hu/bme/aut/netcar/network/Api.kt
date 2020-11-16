package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.User
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getAllUsers")
    fun getUsers() : Call<List<User>>

    @GET("getUser/{id}")
    fun getUser(
        @Path("id") id: Int
    ) : Call<User>

    @POST("addUser")
    fun addNewUser(
        @Body user: User
    ) : Call<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    fun userLogin (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>
}