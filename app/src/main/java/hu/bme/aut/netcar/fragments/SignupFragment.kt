package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.Api
import hu.bme.aut.netcar.network.RetrofitClient
import hu.bme.aut.netcar.network.DefaultResponse
import kotlinx.android.synthetic.main.fragment_login.btnSignUp
import kotlinx.android.synthetic.main.fragment_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignupFragment : Fragment() {

    private lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = RetrofitClient.INSTANCE

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

    private fun isValidEmail(string: String): Boolean{
        val regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(string as CharSequence)
        return matcher.matches()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignUp.setOnClickListener {
            val userNameIn = etNameGiven.text.toString()
            val emailAddressIn = etEmailGiven.text.toString()
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
            else if (emailAddressIn.isEmpty()) {
                etEmailGiven.requestFocus()
                etEmailGiven.error = resources.getString(R.string.btn_sigin_error_email_1)
            }
            else if (!isValidEmail(emailAddressIn)) {
                etEmailGiven.requestFocus()
                etEmailGiven.error = resources.getString(R.string.btn_sigin_error_email_2)
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
                val newUser = UserData(name = etNameGiven.text.toString(), email = etEmailGiven.text.toString(),
                    password = etPasswordGiven.text.toString())

                api.addNewUser(newUser)
                    .enqueue(object : Callback<DefaultResponse> {
                        override fun onResponse(
                            call: Call<DefaultResponse>,
                            response: Response<DefaultResponse>
                        ) {
                            if (response.body()?.message.equals("SUCCESSFUL_REGISTRATION")) {
                                findNavController().navigate(
                                    R.id.action_SignupFragment_to_LoginFragment, null
                                )
                                Toast.makeText(
                                    context,
                                    getString(R.string.successfully_registered),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            if (response.body()?.message.equals("EMAIL_ALREADY_USED")) {
                                Toast.makeText(
                                    context,
                                    "Error: email is already used!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_LONG).show()
                        }

                    })


                /*
                findNavController().navigate(
                    R.id.action_SignupFragment_to_LoginFragment, null
                )
                Toast.makeText(
                    context,
                    getString(R.string.successfully_registered),
                    Toast.LENGTH_LONG
                ).show()
                 */
            }
        }
    }
}