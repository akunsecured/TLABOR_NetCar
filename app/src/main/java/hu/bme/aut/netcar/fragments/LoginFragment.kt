package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignIn.setOnClickListener {
            val email = etEmailAddress.text.toString().trim()
            val password = etPassword.text.toString().trim()

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
                        when (response.body()?.message) {
                            "WRONG_PASSWORD" -> {
                                etPassword.text.clear()
                                etPassword.error = "Wrong password, please try again"
                                etPassword.requestFocus()
                            }

                            "NO_EMAIL_FOUND" -> {
                                etPassword.text.clear()
                                etEmailAddress.error = "There is no account with this email"
                                etEmailAddress.requestFocus()
                            }

                            "SUCCESSFUL_LOGIN" -> {
                                etPassword.text.clear()
                                val userDataId = response.body()!!.id
                                val action = LoginFragmentDirections.actionLoginFragmentToNavigationActivity(userDataId!!)
                                view.findNavController().navigate(action)
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    }

                })
        }
        btnSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_LoginFragment_to_SignupFragment,
                null
            )
        }
    }
}