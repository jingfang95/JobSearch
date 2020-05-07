package edu.utap.jobsearch.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Color
import edu.utap.jobsearch.JobRow
import edu.utap.jobsearch.R
import edu.utap.jobsearch.glide.Glide
import kotlinx.android.synthetic.main.one_company.*
import java.util.*

class OneCompany: AppCompatActivity() {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var appliedJob : JobRow
    private var ownerID: String? = null
    private var id: String? = null
    private var logo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_company)

        val activityThatCalled = intent
        val logoURL = activityThatCalled.extras?.getString("companyLogo")
        logo = logoURL
        if (!logoURL.isNullOrEmpty()) {
            Glide.glideFetch(logoURL.toString(), company_photo)
        }
        one_job_title.text = activityThatCalled.extras?.getString("postTitle")
        one_company_name.text = activityThatCalled.extras?.getString("postCompany")
        one_job_location.text = activityThatCalled.extras?.getString("postLocation")
//        descriptionText = activityThatCalled.extras?.getString("postDescription")
        val b = Bundle()
        b.putString("postDescription", activityThatCalled.extras?.getString("postDescription"))
        val frag = DescriptionFragment()
        frag.arguments = b

        //
        val c = Bundle()
        c.putString("name", activityThatCalled.extras?.getString("postCompany"))
        val cfrag = CompanyReviewFragment()
        cfrag.arguments = c
        //
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment, frag)
            .commit()

        description.isClickable = true
        description.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, frag)
                .commit()

        }
        review.isClickable = true
        review.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, cfrag)
                .commit()
        }

        // situation?
        // find job in database
        var isApplied = false
        ownerID = activityThatCalled.extras?.getString("ownerID")
        val jobID = activityThatCalled.extras?.getString("postID")
        id = jobID
        Log.d("Find job", "start finding")
        db.collection("job")
            .whereEqualTo("ownerUid", ownerID)
            .whereEqualTo("id", jobID)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    return@addSnapshotListener
                }
                val result = querySnapshot!!.documents.mapNotNull {
                    it.toObject(JobRow::class.java)
                }
                if (!result.isNullOrEmpty()) {
                    Log.d("Find job", "applied job")
                    appliedJob = result[0]
                    isApplied = true
                    feedback.visibility = View.VISIBLE
                    apply.text = "Withdrawal"
                    apply.visibility = View.VISIBLE
                }
            }
        if (isApplied) {
            feedback.visibility = View.VISIBLE
            apply.text = "Withdrawal"
            apply.visibility = View.VISIBLE
        } else {
            feedback.visibility = View.INVISIBLE
            apply.text = "Apply Now"
            apply.visibility = View.VISIBLE
        }

        var url = activityThatCalled.extras?.getString("applyURL")
        if (!url.isNullOrEmpty()) {
            url = Html.fromHtml(url).toString()
        }
        apply.setOnClickListener {
            if (!isApplied) {
                // apply
                val applyIntent = Intent(Intent.ACTION_VIEW)
                applyIntent.data = Uri.parse(url)
                val resultCode = 1
                startActivityForResult(applyIntent, resultCode)
            } else {
                // withdrawal
                apply.text = "Apply Now"
                isApplied = false
                feedback.visibility = View.INVISIBLE
                // delete
                db.collection("job").document(appliedJob.rowID).delete()
                    .addOnSuccessListener {
                        Log.d(javaClass.simpleName, "Deleted ${appliedJob.rowID}")
                    }
                    .addOnFailureListener { e ->
                        Log.d(javaClass.simpleName, "Delete FAILED of ${appliedJob.rowID}")
                        Log.w(javaClass.simpleName, "Error deleting document", e)
                    }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Company page", "back from apply website")
        val view = LayoutInflater.from(this).inflate(R.layout.apply_window, null)
        val applyWindow = PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        applyWindow.elevation = 10.0f
        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        applyWindow.enterTransition = slideIn

//        val slideOut = Slide()
//        slideOut.slideEdge = Gravity.BOTTOM
//        applyWindow.exitTransition = slideOut

        val yesBut = view.findViewById<Button>(R.id.feedback_yes)
        yesBut.setOnClickListener {
            feedback.visibility = View.VISIBLE
            apply.text = "Withdrawal"
            // add to database
            appliedJob = JobRow().apply {
                ownerUid = ownerID
            }
            appliedJob.rowID = db.collection("job").document().id
            val jobInfo = hashMapOf(
                "id" to id,
                "company" to one_company_name.text,
                "ownerUid" to ownerID,
                "location" to one_job_location.text,
                "title" to one_job_title.text,
                "company_logo" to logo,
                "timeStamp" to Timestamp(Date()),
                "rowID" to appliedJob.rowID
            )
            db.collection("job").document(appliedJob.rowID)
                .set(jobInfo)
                .addOnSuccessListener {
                    Log.d(javaClass.simpleName, "Updated ${appliedJob.rowID}")
                }
                .addOnFailureListener { e ->
                    Log.d(javaClass.simpleName, "Updated FAILED of ${appliedJob.rowID}")
                    Log.w(javaClass.simpleName, "Error updating document", e)
                }
            applyWindow.dismiss()
        }

        val noBut = view.findViewById<Button>(R.id.feedback_no)
        noBut.setOnClickListener {
            applyWindow.dismiss()
        }

        TransitionManager.beginDelayedTransition(company_layout)
        applyWindow.showAtLocation(company_layout, Gravity.CENTER, 0, 0)
    }
}