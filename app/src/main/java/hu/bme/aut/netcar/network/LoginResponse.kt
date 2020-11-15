package hu.bme.aut.netcar.network

import hu.bme.aut.netcar.data.UserData

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val userData: UserData
)