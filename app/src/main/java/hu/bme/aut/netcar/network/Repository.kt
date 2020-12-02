package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.UserData
import retrofit2.HttpException

object Repository {
    suspend fun getUser(
        id: Int, token: String
    ) : UserData? {
        try {
            val retrofit = RetrofitClientAuth(token)
            return retrofit.INSTANCE.getUserById2(id).userData
        } catch (e: HttpException) {
            if (e.code() == 404) {
                return null
            }

            throw e
        }
    }
}