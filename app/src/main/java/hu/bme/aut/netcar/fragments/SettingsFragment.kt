package hu.bme.aut.netcar.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.android.synthetic.main.dialog_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {

    private lateinit var filepath: Uri
    private lateinit var bitmap: Bitmap
    private var chooseImage = false
    private var userDataId: Int = -1
    private var userData: UserData? = null
    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)

        val id = arguments?.getInt("userDataId")
        userDataId = id!!

        val token = arguments?.getString("token")
        userToken = token!!
    }

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

        if (userData?.picture != null) {
            val imageBytes = Base64.decode(userData?.picture, 0)
            settings_userimage.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.size
                )
            )
        }

        settings_userimage.setOnClickListener {
            startFileChooser()
        }

        settings_btnSave.setOnClickListener {
            var detailChanges = false

            if (detailChanges || chooseImage) {
                val dialogLayout = LayoutInflater.from(view.context).inflate(R.layout.dialog_settings, null)
                val builder = AlertDialog.Builder(view.context).setView(dialogLayout)
                val alertDialog = builder.show()

                dialogLayout.dialog_settings_btnSave.setOnClickListener {
                    when {
                        dialogLayout.etPassword1.text.isEmpty() -> {
                            dialogLayout.etPassword1.requestFocus()
                            dialogLayout.etPassword1.error = getString(R.string.btn_sigin_error_password_1)
                        }
                        dialogLayout.etPassword2.text.isEmpty() -> {
                            dialogLayout.etPassword2.requestFocus()
                            dialogLayout.etPassword2.error = getString(R.string.btn_sigin_error_password_1)
                        }
                        dialogLayout.etPassword1.text.toString() != dialogLayout.etPassword2.text.toString() -> {
                            dialogLayout.etPassword2.requestFocus()
                            dialogLayout.etPassword2.error = getString(R.string.btn_sigin_error_password_4)
                        }
                        else -> {
                            val newUser: UserData? = userData
                            val picture: String?

                            if (chooseImage) {
                                navView.header_image.setImageBitmap(
                                    Bitmap.createScaledBitmap(bitmap, headerImgWidth, headerImgHeight, false))

                                val baos = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos)
                                val b = baos.toByteArray()
                                picture = Base64.encodeToString(b, Base64.DEFAULT)
                                newUser?.picture = picture
                            }

                            var defaultResponse: DefaultResponse?
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    defaultResponse = Repository.updateUser(userDataId, newUser!!, userToken)

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), defaultResponse?.message, Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            }

                            alertDialog.dismiss()
                            Toast.makeText(view.context, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                dialogLayout.dialog_settings_btnCancel.setOnClickListener {
                    if (chooseImage) {
                        settings_userimage.setImageBitmap(null)
                        chooseImage = false
                    }
                    alertDialog.dismiss()
                }
            }

            else {
                Toast.makeText(requireContext(), getString(R.string.no_changes), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startFileChooser() {
        val intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
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