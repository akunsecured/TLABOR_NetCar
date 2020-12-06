package hu.bme.aut.netcar.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientAuth(token: String? = null) {

    private val AUTH = "Bearer $token"
    private val BASE_URL = "https://tlab-netcar.herokuapp.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            var request = chain.request()

            if (!token.isNullOrEmpty()) {
                request = request.newBuilder()
                    .addHeader("Authorization", AUTH)
                    .build()
            }

            chain.proceed(request)
        }.build()

    val gson = GsonBuilder().setLenient().create()

    val INSTANCE: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        retrofit.create(Api::class.java)
    }
}
