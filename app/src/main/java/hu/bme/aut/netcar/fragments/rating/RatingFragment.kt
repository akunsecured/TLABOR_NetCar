package hu.bme.aut.netcar.fragments.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.netcar.R

class RatingFragment : Fragment() {

    private lateinit var ratingViewModel: RatingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ratingViewModel =
            ViewModelProviders.of(this).get(RatingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_rating, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        ratingViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}