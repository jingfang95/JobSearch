package edu.utap.jobsearch.ui.review

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.utap.jobsearch.R
import edu.utap.jobsearch.MainViewModel

class ReviewFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
//    private val frags = InterviewFragment.newInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_review, container, false)
//        val rating = root.findViewById<RatingBar>(R.id.company_rating)
//        val ratingText = root.findViewById<TextView>(R.id.rating_text)
//        rating.setOnRatingBarChangeListener { _, fl, _ ->
//            ratingText.text = when (fl) {
//                0f -> "Choose Rating"
//                1f -> "Very Dissatisfied"
//                2f -> "Dissatisfied"
//                3f -> "Neutral"
//                4f -> "Satisfied"
//                else -> "Very Satisfied"
//            }
//        }
        this.requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment, InterviewFragment())
            .commit()
//        val stub = root.findViewById<ViewStub>(R.id.layout_stub)
//        stub.layoutResource = R.layout.interview_review
//        stub.inflate()

        val interview = root.findViewById<Button>(R.id.interview_selection)
        val interviewSelected = root.findViewById<TextView>(R.id.interview_line)
        val company = root.findViewById<Button>(R.id.company_selection)
        val companySelected = root.findViewById<TextView>(R.id.company_line)
        interviewSelected.background = Color.parseColor("#90caf9").toDrawable()
        interview.setOnClickListener {
            interviewSelected.background = Color.parseColor("#90caf9").toDrawable()
            companySelected.background = Color.TRANSPARENT.toDrawable()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, InterviewFragment())
                .commit()
        }
        company.setOnClickListener {
            interviewSelected.background = Color.TRANSPARENT.toDrawable()
            companySelected.background = Color.parseColor("#90caf9").toDrawable()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, CompanyFragment())
                .commit()
        }
        return root
    }
}
