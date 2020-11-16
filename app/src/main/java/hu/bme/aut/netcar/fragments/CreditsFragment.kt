package hu.bme.aut.netcar.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import hu.bme.aut.netcar.NavigationActivity
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.RetrofitClient
import kotlinx.android.synthetic.main.fragment_credits.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreditsFragment : Fragment() {

    private var parentActivity: NavigationActivity? = null
    private var userDataId: Int = -1
    private var userData: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)

        parentActivity = activity as NavigationActivity

        userDataId = parentActivity!!.intent.getIntExtra(NavigationActivity.USERDATA_ID, -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        val view = inflater.inflate(R.layout.fragment_credits, container, false)

        RetrofitClient.INSTANCE.getUserById(userDataId)
            .enqueue(object: Callback<UserData> {
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    userData = response.body()

                    tvCreditAmount.text = ("$ ").plus(userData!!.credits.toString())
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Toast.makeText(context!!, "Something went wrong.", Toast.LENGTH_LONG)
                        .show()
                }

            })

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCredits.setOnClickListener {
            val credits = tvCreditAmount.text.toString().removeRange(0, 1).toInt()
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
            builder.setView(input)
                .setNeutralButton(getString(R.string.dialog_button_adding_credits)) { _, _ ->
                    if(input.text.isEmpty()) {
                        tvCreditAmount.text =
                            "$" + (credits + 0).toString()
                    }
                    else{
                        tvCreditAmount.text =
                            "$" + (credits + input.text.toString().toInt()).toString()
                    }
                }

            val dialog = builder.create()

            dialog.show()
        }
    }
}