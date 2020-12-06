package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.CarData
import hu.bme.aut.netcar.data.JwtRequest
import hu.bme.aut.netcar.data.UserDTO
import hu.bme.aut.netcar.data.UserData
import retrofit2.HttpException

object Repository {
    suspend fun getUser(id: Int, token: String) : UserData? {
        try {
            val retrofit = RetrofitClientAuth(token)
            return retrofit.INSTANCE.getUserById(id)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                return null
            }

            throw exception
        }
    }

    suspend fun updateCar(id: Int, brand: String, model: String,
                          serial: String, pic: String, hasBoot: Boolean,
                          seats: Int, placeInBoot: Int, token: String) : DefaultResponse? {
        try {
            val retrofit = RetrofitClientAuth(token)
            return retrofit.INSTANCE.updateCar(id, brand, model, serial, pic, hasBoot, seats, placeInBoot)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                return null
            }

            throw exception
        }
    }

    suspend fun getCar(id: Int, token: String) : CarData? {
        try {
            val retrofit = RetrofitClientAuth(token)
            return retrofit.INSTANCE.getCarById(id)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                return null
            }

            throw exception
        }
    }

    suspend fun updateUser(id: Int, userData: UserData, token: String) : DefaultResponse? {
        try {
            val retrofit = RetrofitClientAuth(token)
            return retrofit.INSTANCE.updateUser(id, userData)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                return null
            }

            if (exception.code() == 401) {
                return DefaultResponse("Unauthorized error")
            }

            throw exception
        }
    }

    suspend fun userLogin(authenticationRequest: JwtRequest) : DefaultResponse? {
        try {
            val retrofit = RetrofitClientAuth()
            return retrofit.INSTANCE.userLogin(authenticationRequest)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                return null
            }

            throw exception
        }
    }

    suspend fun register(userDTO: UserDTO) : DefaultResponse? {
        try {
            val retrofit = RetrofitClientAuth()
            return retrofit.INSTANCE.register(userDTO)
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                return null
            }

            throw exception
        }
    }
}