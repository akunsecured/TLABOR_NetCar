package hu.bme.aut.netcar.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import hu.bme.aut.netcar.R
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.nav_header_main.*

class SettingsFragment : Fragment() {

    private lateinit var filepath: Uri
    private lateinit var bitmap: Bitmap
    private var chooseImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        val header_emailText = header_email.text
        val header_nameText = header_name.text

        settings_etEmail.hint = header_emailText
        settings_etProfileName.hint = header_nameText
         */

        settings_userimage.setOnClickListener {
            startFileChooser()
        }

        settings_btnSave.setOnClickListener {
            var detailChanges = false
            if (settings_etEmail.text != null || settings_etPassword.text != null
                || settings_etProfileName.text != null)
                detailChanges = true

            if (detailChanges) {
                val builder = AlertDialog.Builder(view.context).setTitle("Confirm your current password")
                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                val etPw1 = EditText(context)
                var etPw2 = EditText(context)
                etPw1.layoutParams = lp
                etPw2.layoutParams = lp
                etPw1.hint = "Password"
                etPw2.hint = "Confirm Password"

                linearLayout.addView(etPw1)
                linearLayout.addView(etPw2)

                builder.show()
            }

            if (chooseImage) {
                header_image.setImageBitmap(bitmap)
            }
        }
    }

    private fun startFileChooser() {
        var intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            val contentResolver = requireActivity().contentResolver
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            settings_userimage.setImageBitmap(bitmap)
            chooseImage = true
        }
    }
}