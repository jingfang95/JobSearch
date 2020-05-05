package edu.utap.jobsearch.ui.home

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ReviewRow
import java.util.*

class DescriptionFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the root view and cache references to vital UI elements
        val root = inflater.inflate(R.layout.job_description, container, false)
        val description = root.findViewById<TextView>(R.id.description)
        if (!arguments?.getString("postDescription").isNullOrEmpty()) {
            description.text = Html.fromHtml(arguments?.getString("postDescription"))
        }
        description.movementMethod = ScrollingMovementMethod()
        return root
    }

}
