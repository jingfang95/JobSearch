package edu.utap.jobsearch.ui.review

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.utap.jobsearch.R

class ReviewFragment : Fragment() {

    private lateinit var reviewViewModel: ReviewViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        reviewViewModel =
                ViewModelProviders.of(this).get(ReviewViewModel::class.java)
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
        val interview = root.findViewById<Button>(R.id.interview_selection)
        val interviewSelected = root.findViewById<TextView>(R.id.interview_line)
        val company = root.findViewById<Button>(R.id.company_selection)
        val companySelected = root.findViewById<TextView>(R.id.company_line)
        interviewSelected.background = Color.parseColor("#90caf9").toDrawable()
        interview.setOnClickListener {
            interviewSelected.background = Color.parseColor("#90caf9").toDrawable()
            companySelected.background = Color.TRANSPARENT.toDrawable()
        }
        company.setOnClickListener {
            interviewSelected.background = Color.TRANSPARENT.toDrawable()
            companySelected.background = Color.parseColor("#90caf9").toDrawable()
        }
        return root
    }
}
