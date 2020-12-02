package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserDTO
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.android.synthetic.main.fragment_login.btnSignUp
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_top)
        exitTransition = inflater.inflateTransition(R.transition.slide_bottom)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_signup, container, false)

    private fun isLettersOrNumbers(string: String): Boolean {
        for(c in string){
            if(c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9'){
                return false
            }
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignUp.setOnClickListener {
            val userNameIn = etNameGiven.text.toString()
            val passwordIn = etPasswordGiven.text.toString()
            val confpasswordIn = etPasswordConfGiven.text.toString()

            if (userNameIn.isEmpty()) {
                etNameGiven.requestFocus()
                etNameGiven.error = resources.getString(R.string.btn_sigin_error_username_1)
            }
            else if (!isLettersOrNumbers(userNameIn)){
                etNameGiven.requestFocus()
                etNameGiven.error = resources.getString(R.string.btn_sigin_error_username_3)
            }
            else if (userNameIn.length < 3) {
                etNameGiven.requestFocus()
                etNameGiven.error = resources.getString(R.string.btn_sigin_error_username_2)
            }
            else if (passwordIn.isEmpty()) {
                etPasswordGiven.requestFocus()
                etPasswordGiven.error = resources.getString(R.string.btn_sigin_error_password_1)
            }
            else if (!isLettersOrNumbers(passwordIn)) {
                etPasswordGiven.requestFocus()
                etPasswordGiven.text.clear()
                etPasswordGiven.error = resources.getString(R.string.btn_sigin_error_password_3)
            }
            else if (userNameIn.length < 3) {
                etPasswordGiven.requestFocus()
                etPasswordGiven.text.clear()
                etPasswordGiven.error = resources.getString(R.string.btn_sigin_error_password_2)
            }
            else if (passwordIn != confpasswordIn){
                etPasswordGiven.requestFocus()
                etPasswordGiven.text.clear()
                etPasswordGiven.error = resources.getString(R.string.btn_sigin_error_password_4)
            }
            else {
                var defaultResponse: DefaultResponse?
                val newUser = UserDTO(etNameGiven.text.toString(), etPasswordGiven.text.toString())

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        defaultResponse = Repository.register(newUser)

                        withContext(Dispatchers.Main) {
                            when (defaultResponse?.message) {
                                "Successful registration" -> {
                                    findNavController().navigate(
                                        R.id.action_SignupFragment_to_LoginFragment, null
                                    )
                                    Toast.makeText(
                                        context,
                                        getString(R.string.successfully_registered),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                "Username already used" -> {
                                    Toast.makeText(
                                        context,
                                        "Error: username is already used!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
                /*retrofit = RetrofitClientAuth()
                retrofit.INSTANCE.register(newUser)
                    .enqueue(object : Callback<DefaultResponse> {
                        override fun onResponse(
                            call: Call<DefaultResponse>,
                            response: Response<DefaultResponse>
                        ) {
                            if (response.body()?.message.equals("Successful registration")) {
                                findNavController().navigate(
                                    R.id.action_SignupFragment_to_LoginFragment, null
                                )
                                Toast.makeText(
                                    context,
                                    getString(R.string.successfully_registered),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            if (response.body()?.message.equals("Username already used")) {
                                Toast.makeText(
                                    context,
                                    "Error: username is already used!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_LONG).show()
                        }

                    })*/
            }
        }
    }
}