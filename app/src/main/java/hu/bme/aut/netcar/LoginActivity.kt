package hu.bme.aut.netcar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Login megvalósítása, hibakezelés
        btnSignIn.setOnClickListener {
            var emailAddressIn = etEmailAddress.text.toString()
            var passwordIn = etPassword.text.toString()

            if (emailAddressIn.isEmpty()) {
                etEmailAddress.requestFocus()
                etEmailAddress.error = getResources().getString(R.string.btn_sigin_error_email_1)
            }
            else if (!emailAddressIn.contains("@") || !emailAddressIn.contains(".")) {
            etEmailAddress.requestFocus()
            etEmailAddress.error = getResources().getString(R.string.btn_sigin_error_email_2)
            }
            else if (passwordIn.isEmpty()) {
                etPassword.requestFocus()
                etPassword.error = getResources().getString(R.string.btn_sigin_error_password_1)
            }
            else if (passwordIn != "123") {
                etPassword.requestFocus()
                etPassword.text.clear()
                etPassword.error = getResources().getString(R.string.btn_sigin_error_password_2)
            }
            else {
                // Login megvalósítása


                startActivity(Intent(this, ReadFromJsonActivity::class.java))
            }
        }

        // Signup activity-re való váltás
        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}