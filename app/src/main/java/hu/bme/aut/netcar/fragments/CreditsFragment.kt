package hu.bme.aut.netcar.fragments

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import hu.bme.aut.netcar.R
import kotlinx.android.synthetic.main.fragment_credits.*

class CreditsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.fragment_credits, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCredits.setOnClickListener {
            var credits = tvCreditAmount.text.toString().removeRange(0, 1).toInt()
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