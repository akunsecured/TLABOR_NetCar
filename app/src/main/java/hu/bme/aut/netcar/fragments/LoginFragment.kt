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
import hu.bme.aut.netcar.network.LoginResponse
import hu.bme.aut.netcar.network.RetrofitClient
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val email = etEmailAddress.text.toString().trim()
        val password = etPassword.text.toString().trim()

        btnSignIn.setOnClickListener {
            if (email.isEmpty()) {
                etEmailAddress.error = getString(R.string.btn_sigin_error_email_1)
                etEmailAddress.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = getString(R.string.btn_sigin_error_password_1)
                etPassword.requestFocus()
                return@setOnClickListener
            }

            RetrofitClient.INSTANCE.userLogin(email, password)
                .enqueue(object: Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        TODO("Not yet implemented")
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    }

                })

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