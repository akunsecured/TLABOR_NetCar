package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.DataResult
import hu.bme.aut.netcar.data.User
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("/greeting")
    fun getDetails() : Call<List<DataResult>>

    @GET("getAllUsers")
    fun getUsers() : Call<List<User>>

    @POST("addUser")
    fun addNewUser(
        @Body user: User
    ) : Call<StringResponse>

    /* TODO: userLogin
    @FormUrlEncoded
    @POST("userlogin")
    fun userLogin (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<StringResponse>
     */
}