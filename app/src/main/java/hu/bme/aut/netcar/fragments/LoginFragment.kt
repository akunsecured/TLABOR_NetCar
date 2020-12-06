package hu.bme.aut.netcar.fragments

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.NavigationActivity
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.JwtRequest
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

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

        etUsername.setText(arguments?.getString("username"))
        etPassword.setText(arguments?.getString("password"))

        btnSignIn.setOnClickListener {
            val username = etUsername.text.toString().toLowerCase(Locale.ROOT).trim()
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

                                val userToken: String = message!!.split(' ')[0]
                                val userDataId: Int = message.split(' ')[1].toInt()

                                withContext(Dispatchers.IO) {
                                    val userData = Repository.getUser(userDataId, userToken)
                                    userData?.picture = ""

                                    val bundle = bundleOf(
                                        "userToken" to userToken,
                                        "userDataId" to userDataId
                                    )

                                    bundle.putSerializable("userData", userData)

                                    withContext(Dispatchers.Main) {

                                        val intent = Intent(requireContext(), NavigationActivity::class.java)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        btnSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_LoginFragment_to_SignupFragment,
                null
            )
        }
    }
}