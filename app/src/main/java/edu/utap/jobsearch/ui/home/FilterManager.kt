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

class FilterManager: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_window)

        val activityThatCalled = intent
//        val logoURL = activityThatCalled.extras?.getString("companyLogo")
    }

}