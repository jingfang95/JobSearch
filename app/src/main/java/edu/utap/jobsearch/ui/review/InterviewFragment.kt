package edu.utap.jobsearch.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.utap.jobsearch.R

class InterviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the root view and cache references to vital UI elements
        return inflater.inflate(R.layout.interview_review, container, false)
    }

}
