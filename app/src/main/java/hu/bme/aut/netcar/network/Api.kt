package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.DataResult
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("/greeting")
    fun getDetails() : Call<List<DataResult>>

    @FormUrlEncoded
    @POST("/adduser")
    fun createUser (
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("pictureUrl") pictureUrl: String
    ) : Call<DefaultResponse>

    @FormUrlEncoded
    @POST("userlogin")
    fun userLogin (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>
}