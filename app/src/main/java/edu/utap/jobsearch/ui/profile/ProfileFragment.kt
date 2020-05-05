package edu.utap.jobsearch.ui.profile

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.scale
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.utap.jobsearch.R

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val account = root.findViewById<Button>(R.id.account)
        val s1 = SpannableStringBuilder().bold { scale(1.2f) { append("Account") } }.append("\nSet profile preferences to improve recommendations")
        account.text = s1
        val jobs = root.findViewById<Button>(R.id.jobs)
        val s2 = SpannableStringBuilder().bold { scale(1.2f) { append("Jobs") } }.append("\nView jobs you've applied")
        jobs.text = s2
        val reviews = root.findViewById<Button>(R.id.reviews)
        val s3 = SpannableStringBuilder().bold { scale(1.2f) { append("Reviews") } }.append("\nSee reviews you've posted")
        reviews.text = s3
        return root
    }
}
