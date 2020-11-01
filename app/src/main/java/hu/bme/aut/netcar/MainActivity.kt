package hu.bme.aut.netcar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.netcar.fragments.LoginFragment
import hu.bme.aut.netcar.fragments.SignupFragment
import java.lang.Exception

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