package edu.utap.jobsearch.ui.home

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.utap.jobsearch.R
import edu.utap.jobsearch.glide.Glide
import kotlinx.android.synthetic.main.one_company.*

class OneCompany: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_company)

        val activityThatCalled = intent
        val logoURL = activityThatCalled.extras?.getString("companyLogo")
        if (!logoURL.isNullOrEmpty()) {
            Glide.glideFetch(logoURL.toString(), company_photo)
        }
        one_job_title.text = activityThatCalled.extras?.getString("postTitle")
        one_company_name.text = activityThatCalled.extras?.getString("postCompany")
        one_job_location.text = activityThatCalled.extras?.getString("postLocation")
        description_box.text = Html.fromHtml(activityThatCalled.extras?.getString("postDescription"))
        description_box.movementMethod = ScrollingMovementMethod()
    }
}