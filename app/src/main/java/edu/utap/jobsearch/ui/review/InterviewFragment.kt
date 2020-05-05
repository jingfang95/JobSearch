package edu.utap.jobsearch.ui.review

import android.os.Bundle
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

class InterviewFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var review : ReviewRow

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the root view and cache references to vital UI elements
        val root = inflater.inflate(R.layout.interview_review, container, false)
        val submitBut = root.findViewById<Button>(R.id.submitBut)
        val rating = root.findViewById<RatingBar>(R.id.rating)
        var ratingText : String? = null
        rating.setOnRatingBarChangeListener { _, fl, _ ->
            ratingText = when (fl) {
                0f -> "Choose Rating"
                1f -> "Very Dissatisfied"
                2f -> "Dissatisfied"
                3f -> "Neutral"
                4f -> "Satisfied"
                else -> "Very Satisfied"
            }
        }
        val companyName = root.findViewById<EditText>(R.id.companyName)
        val reviewTitle = root.findViewById<EditText>(R.id.reviewTitle)
        val cb1 = root.findViewById<CheckBox>(R.id.cb1)
        val cb2 = root.findViewById<CheckBox>(R.id.cb2)
        val cb3 = root.findViewById<CheckBox>(R.id.cb3)
        var type: String? = null
        cb1.setOnCheckedChangeListener { _, b ->
            if (b) {
                type = "Phone Screen"
                cb2.isChecked = false
                cb3.isChecked = false
            }
        }
        cb2.setOnCheckedChangeListener { _, b ->
            if (b) {
                type = "1:1"
                cb1.isChecked = false
                cb3.isChecked = false
            }
        }
        cb3.setOnCheckedChangeListener { _, b ->
            if (b) {
                type = "Group/Panel"
                cb2.isChecked = false
                cb1.isChecked = false
            }
        }
        val description = root.findViewById<EditText>(R.id.description)
        val comment = root.findViewById<EditText>(R.id.comment)
        submitBut.setOnClickListener {
            review = ReviewRow().apply {
                ownerUid = viewModel.myUid()
            }
            review.rowID = db.collection("user").document().id
            val reviewInfo = hashMapOf(
                "name" to companyName.text.toString(),
                "title" to reviewTitle.text.toString(),
                "rating" to ratingText.toString(),
                "type" to type.toString(),
                "ownerUid" to viewModel.myUid(),
                "description" to description.text.toString(),
                "comment" to comment.text.toString(),
                "timeStamp" to Timestamp(Date()),
                "rowID" to review.rowID
            )
            db.collection("review").document(review.rowID)
                .set(reviewInfo)
                .addOnSuccessListener {
                    Log.d(javaClass.simpleName, "Updated ${review.rowID}")
                }
                .addOnFailureListener { e ->
                    Log.d(javaClass.simpleName, "Updated FAILED of ${review.rowID}")
                    Log.w(javaClass.simpleName, "Error updating document", e)
                }
            companyName.text.clear()
            reviewTitle.text.clear()
            rating.rating = 1f
            cb1.isChecked = false
            cb2.isChecked = false
            cb3.isChecked = false
            description.text.clear()
            comment.text.clear()
            Toast.makeText(activity, "Submitted", Toast.LENGTH_SHORT).show()
        }
        return root
    }

}
