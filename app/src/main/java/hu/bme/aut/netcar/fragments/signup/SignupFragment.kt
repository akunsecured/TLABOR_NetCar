package hu.bme.aut.netcar.fragments.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.R
import kotlinx.android.synthetic.main.fragment_login.btnSignUp
import kotlinx.android.synthetic.main.fragment_signup.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_signup, container, false)

    fun isLettersOrNumbers(string: String): Boolean {
        for(c in string){
            if(c !in 'A'..'Z' && c !in 'a'..'z' && c !in '0'..'9'){
                return false
            }
        }
        return true;
    }

    fun isValidEmail(string: String): Boolean{
        val regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(string as CharSequence)
        return matcher.matches()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignUp.setOnClickListener {
            var userNameIn = etNameGiven.text.toString()
            var emailAddressIn = etEmailGiven.text.toString()
            var passwordIn = etPasswordGiven.text.toString()
            var confpasswordIn = etPasswordConfGiven.text.toString()

            if (userNameIn.isEmpty()) {
                etNameGiven.requestFocus()
                etNameGiven.error = getResources().getString(R.string.btn_sigin_error_username_1)
            }
            else if (!isLettersOrNumbers(userNameIn)){
                etNameGiven.requestFocus()
                etNameGiven.error = getResources().getString(R.string.btn_sigin_error_username_3)
            }
            else if (userNameIn.length < 3) {
                etNameGiven.requestFocus()
                etNameGiven.error = getResources().getString(R.string.btn_sigin_error_username_2)
            }
            else if (emailAddressIn.isEmpty()) {
                etEmailGiven.requestFocus()
                etEmailGiven.error = getResources().getString(R.string.btn_sigin_error_email_1)
            }
            else if (!isValidEmail(emailAddressIn)) {
                etEmailGiven.requestFocus()
                etEmailGiven.error = getResources().getString(R.string.btn_sigin_error_email_2)
            }
            else if (passwordIn.isEmpty()) {
                etPasswordGiven.requestFocus()
                etPasswordGiven.error = getResources().getString(R.string.btn_sigin_error_password_1)
            }
            else if (!isLettersOrNumbers(passwordIn)) {
                etPasswordGiven.requestFocus()
                etPasswordGiven.text.clear()
                etPasswordGiven.error = getResources().getString(R.string.btn_sigin_error_password_3)
            }
            else if (userNameIn.length < 3) {
                etPasswordGiven.requestFocus()
                etPasswordGiven.text.clear()
                etPasswordGiven.error = getResources().getString(R.string.btn_sigin_error_password_2)
            }
            else if (passwordIn != confpasswordIn){
                etPasswordGiven.requestFocus()
                etPasswordGiven.text.clear()
                etPasswordGiven.error = getResources().getString(R.string.btn_sigin_error_password_4)
            }
            else {
                findNavController().navigate(
                    R.id.action_SignupFragment_to_LoginFragment, null
                )
                Toast.makeText(
                    context,
                    getString(R.string.successfully_registered),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}