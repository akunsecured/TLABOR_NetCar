package hu.bme.aut.netcar.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DataResult(val id: Int?, val content: String?, val rendszam: String?, val kerek: String?, val ules: String?, val ev: String?, val marka: String?)