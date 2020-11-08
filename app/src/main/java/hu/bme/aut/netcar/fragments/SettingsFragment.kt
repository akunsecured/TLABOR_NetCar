package hu.bme.aut.netcar.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import hu.bme.aut.netcar.R
import kotlinx.android.synthetic.main.dialog_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


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

        val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)
        val headerImgWidth = navView.header_image.width
        val headerImgHeight = navView.header_image.height

        settings_etEmail.hint = navView.header_email.text
        settings_etProfileName.hint = navView.header_name.text

        settings_userimage.setOnClickListener {
            startFileChooser()
        }

        settings_btnSave.setOnClickListener {
            var detailChanges = false
            if (settings_etEmail.text.isNotEmpty() || settings_etPassword.text.isNotEmpty()
                || settings_etProfileName.text.isNotEmpty()) {
                detailChanges = true
            }

            if (detailChanges) {
                val dialogLayout = LayoutInflater.from(view.context).inflate(R.layout.dialog_settings, null)
                val builder = AlertDialog.Builder(view.context).setView(dialogLayout)
                val alertDialog = builder.show()

                dialogLayout.dialog_settings_btnSave.setOnClickListener {
                    val pw: String
                    if (dialogLayout.etPassword1.text.isEmpty()) {
                        dialogLayout.etPassword1.requestFocus()
                        dialogLayout.etPassword1.error = getString(R.string.btn_sigin_error_password_1)
                    }
                    else if (dialogLayout.etPassword2.text.isEmpty()) {
                        dialogLayout.etPassword2.requestFocus()
                        dialogLayout.etPassword2.error = getString(R.string.btn_sigin_error_password_1)
                    }
                    else if (dialogLayout.etPassword1.text.toString() != dialogLayout.etPassword2.text.toString()) {
                        dialogLayout.etPassword2.requestFocus()
                        dialogLayout.etPassword2.error = getString(R.string.btn_sigin_error_password_4)
                    }
                    else {
                        if (chooseImage) {
                            navView.header_image.setImageBitmap(
                                Bitmap.createScaledBitmap(bitmap, headerImgWidth, headerImgHeight, false))
                            settings_userimage.setImageBitmap(null)
                        }
                        if (settings_etEmail.text.isNotEmpty()) {
                            navView.header_email.text = settings_etEmail.text
                        }
                        if (settings_etProfileName.text.isNotEmpty()) {
                            navView.header_name.text = settings_etProfileName.text
                        }
                        alertDialog.dismiss()
                        Toast.makeText(view.context, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
                        clearSettings(view)
                    }
                }

                dialogLayout.dialog_settings_btnCancel.setOnClickListener {
                    clearSettings(view)
                    if (chooseImage) {
                        settings_userimage.setImageBitmap(null)
                    }
                    alertDialog.dismiss()
                }
            }

            else if (chooseImage) {
                Toast.makeText(view.context, getString(R.string.image_saved), Toast.LENGTH_LONG).show()
                navView.header_image.setImageBitmap(
                    Bitmap.createScaledBitmap(bitmap, headerImgWidth, headerImgHeight, false))
                settings_userimage.setImageBitmap(null)
            }

            else {
                Toast.makeText(view.context, getString(R.string.no_changes), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearSettings(view: View) {
        if (view.settings_etEmail.text.isNotEmpty()) {
            view.settings_etEmail.text.clear()
        }
        if (view.settings_etProfileName.text.isNotEmpty()) {
            view.settings_etProfileName.text.clear()
        }
        if (view.settings_etPassword.text.isNotEmpty()) {
            view.settings_etPassword.text.clear()
        }
    }

    private fun startFileChooser() {
        var intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_picture)),
            111
        )
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