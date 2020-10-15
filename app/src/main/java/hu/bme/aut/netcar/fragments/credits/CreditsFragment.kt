package hu.bme.aut.netcar.fragments.credits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.netcar.R

class CreditsFragment : Fragment() {

    private lateinit var creditsViewModel: CreditsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        creditsViewModel =
            ViewModelProviders.of(this).get(CreditsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_credits, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        creditsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}