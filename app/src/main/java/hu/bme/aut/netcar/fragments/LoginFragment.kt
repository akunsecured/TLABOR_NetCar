package hu.bme.aut.netcar.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.NavigationActivity
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.DataResult
import hu.bme.aut.netcar.network.DriverAPI
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class LoginFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val inflater = TransitionInflater.from(requireContext())
            exitTransition = inflater.inflateTransition(R.transition.fade)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignIn.setOnClickListener {
            val intent = Intent(this.requireContext(), NavigationActivity::class.java)
            intent.putExtra(NavigationActivity.USER_ID, 123)
            startActivity(intent)
        }
        btnSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_LoginFragment_to_SignupFragment,
                null
            )
        }
    }
}