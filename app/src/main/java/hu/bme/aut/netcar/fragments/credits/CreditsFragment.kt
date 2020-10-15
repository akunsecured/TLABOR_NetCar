package hu.bme.aut.netcar.fragments.credits

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import hu.bme.aut.netcar.R
import kotlinx.android.synthetic.main.fragment_credits.*

class CreditsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.fragment_credits, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddCredits.setOnClickListener {
            var credits = tvCreditAmount.text.toString().removeRange(0, 1).toInt()
            val alertDialog : AlertDialog.Builder = AlertDialog.Builder(view.context).setTitle("ADDING CREDITS").setMessage("Enter credits:")
            val input = EditText(context)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            lp.setMargins(4, 2, 4, 2)
            input.layoutParams = lp
            input.inputType = InputType.TYPE_CLASS_NUMBER
            alertDialog.setView(input)
            alertDialog.setNeutralButton("Add"
            ) { _, _ ->
                tvCreditAmount.text = "$" + (credits + input.text.toString().toInt()).toString()
            }
            alertDialog.show()
        }
    }
}