package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.DataResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DriverAPI {
    @GET("/greeting")
    fun getDetails() : Call<List<DataResult>>
}