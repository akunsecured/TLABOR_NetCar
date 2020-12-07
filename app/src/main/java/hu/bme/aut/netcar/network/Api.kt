package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.*
import retrofit2.http.*

interface Api {
    @GET("getAllUsers")
    suspend fun getAllUsers() : List<UserData>

    @GET("getUser/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ) : UserData

    @GET("getCar/{id}")
    suspend fun getCarById(
        @Path("id") id: Int
    ) : CarData

    @GET("getAllCars")
    suspend fun getAllCars() : List<CarData>

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

    @PUT("/getUser/{id}/updateCar")
    suspend fun updateCar(
        @Path("id") id: Int,
        @Body carData: CarData
    ) : DefaultResponse

    @POST("addRequest")
    suspend fun addRequest(
        @Body sr: ServiceRequest
    ) : DefaultResponse

    @GET("getRequestsByDriver/{id}")
    suspend fun getRequestsByDriver(
        @Path("id") id: Int
    ) : List<ServiceRequest>

    @GET("getRequestsByPassenger/{id}")
    suspend fun getRequestsByPassenger(
        @Path("id") id: Int
    ) : List<ServiceRequest>

    @PUT("updateRequest")
    suspend fun updateRequest(
        @Body newer: ServiceRequest
    ) : DefaultResponse

    @GET("getUserPicture/{id}")
    suspend fun getUserPicture(
        @Path("id") id: Int
    ) : String
}