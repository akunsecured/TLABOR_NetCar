package hu.bme.aut.netcar.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.InputType
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.android.synthetic.main.fragment_credits.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreditsFragment : Fragment() {

    private var userDataId: Int = -1
    private var userData: UserData? = null
    private var userToken: String = ""
    private lateinit var runnable: Runnable
    private val handler: Handler = Handler(Looper.getMainLooper())

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCredits.setOnClickListener {
            val credits = tvCreditAmount.text.toString().split(' ')[1].toInt()
            val builder : AlertDialog.Builder = AlertDialog.Builder(view.context).setTitle(getString(
                            R.string.dialog_title_adding_credits)).setMessage(getString(R.string.dialog_message_adding_credits))
            val input = EditText(context)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            lp.setMargins(4, 2, 4, 2)
            input.layoutParams = lp
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.filters = arrayOf(InputFilter.LengthFilter(5))
            builder.setView(input)
                .setNeutralButton(getString(R.string.dialog_button_adding_credits)) { _, _ ->
                    if(input.text.isNotEmpty()) {

                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                val newCredit: Int = input.text.toString().toInt()
                                userData!!.credits = userData!!.credits?.plus(newCredit)
                                val defaultResponse: DefaultResponse? =
                                    Repository.updateUser(userDataId, userData!!, userToken)

                                withContext(Dispatchers.Main) {
                                    tvCreditAmount.text =
                                        "$ " + (credits + input.text.toString().toInt()).toString()
                                    Toast.makeText(requireContext(), defaultResponse?.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                    }
                    else {
                        tvCreditAmount.text =
                            "$ " + (credits + 0).toString()
                    }
                }

            val dialog = builder.create()

            dialog.show()
        }
    }
    private fun updateDetailsCyclic() {
        runnable = Runnable {
            if(!userToken.isBlank()){
                updateUserData()
            }
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)
    }

    private fun updateUserData() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userData = Repository.getUser(userDataId, userToken)
                withContext(Dispatchers.Main) {
                    tvCreditAmount.text = ("$ ").plus(userData!!.credits.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateDetailsCyclic()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }
}