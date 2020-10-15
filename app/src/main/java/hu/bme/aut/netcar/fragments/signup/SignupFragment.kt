package hu.bme.aut.netcar.fragments.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import hu.bme.aut.netcar.R
import kotlinx.android.synthetic.main.fragment_login.*

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_SignupFragment_to_LoginFragment, null
            )

            Toast.makeText(context, "You have successfully registered. Now you can login to use the app!", Toast.LENGTH_LONG).show()
        }
    }
}