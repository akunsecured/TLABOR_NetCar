package hu.bme.aut.netcar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import hu.bme.aut.netcar.data.DataResult
import hu.bme.aut.netcar.network.DriverAPI
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.lang.Exception
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Thread.sleep(200)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}