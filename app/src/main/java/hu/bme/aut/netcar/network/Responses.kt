package hu.bme.aut.netcar.network

data class DefaultResponse (
    val message: String
)

data class LoginResponse (
    val message: String,
    val id: Int?
)