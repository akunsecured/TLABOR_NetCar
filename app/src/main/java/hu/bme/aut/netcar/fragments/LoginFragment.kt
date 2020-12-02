package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.JwtRequest
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = getString(R.string.btn_sigin_error_username_1)
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = getString(R.string.btn_sigin_error_password_1)
                etPassword.requestFocus()
                return@setOnClickListener
            }

            var defaultResponse: DefaultResponse?
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    defaultResponse = Repository.userLogin(JwtRequest(username = username, password = password))

                    withContext(Dispatchers.Main) {
                        when (defaultResponse?.message) {
                            "Wrong username or password" -> {
                                etPassword.text.clear()
                                etPassword.error = "Wrong password, please try again"
                                etPassword.requestFocus()
                            }

                            "Username not found" -> {
                                etPassword.text.clear()
                                etUsername.error = "There is no account with this username"
                                etUsername.requestFocus()
                            }

                            else -> {
                                etPassword.text.clear()
                                val message = defaultResponse?.message
                                val action = LoginFragmentDirections.actionLoginFragmentToNavigationActivity(message!!)
                                view.findNavController().navigate(action)
                            }
                        }
                    }
                }
            }

            /*
            val retrofit = RetrofitClientAuth()
            retrofit.INSTANCE.userLogin(JwtRequest(username = username, password = password))
                .enqueue(object: Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        when (response.body()?.message) {
                            "Wrong username or password" -> {
                                etPassword.text.clear()
                                etPassword.error = "Wrong password, please try again"
                                etPassword.requestFocus()
                            }

                            "Username not found" -> {
                                etPassword.text.clear()
                                etUsername.error = "There is no account with this username"
                                etUsername.requestFocus()
                            }

                            else -> {
                                etPassword.text.clear()
                                val message = response.body()!!.message
                                val action = LoginFragmentDirections.actionLoginFragmentToNavigationActivity(message!!)
                                view.findNavController().navigate(action)
                            }
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    }

                })*/
        }
        btnSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_LoginFragment_to_SignupFragment,
                null
            )
        }
    }
}