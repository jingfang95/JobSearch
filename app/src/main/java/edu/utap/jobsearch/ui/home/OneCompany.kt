package edu.utap.jobsearch.ui.home

import android.content.Intent
import android.net.Uri
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

        var url = activityThatCalled.extras?.getString("applyURL")
        if (!url.isNullOrEmpty()) {
            url = Html.fromHtml(url).toString()
        }
        apply.setOnClickListener {
            val applyIntent = Intent(Intent.ACTION_VIEW)
            applyIntent.data = Uri.parse(url)
            startActivity(applyIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}