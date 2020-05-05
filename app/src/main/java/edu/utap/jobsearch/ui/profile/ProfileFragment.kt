package edu.utap.jobsearch.ui.profile

import android.content.Intent
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.utap.jobsearch.MainActivity
import edu.utap.jobsearch.R
import edu.utap.jobsearch.MainViewModel

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        // user info
        val username = root.findViewById<TextView>(R.id.username)
        if (!viewModel.myDisplayName().isNullOrEmpty()) {
            username.text = viewModel.myDisplayName().toString()
        }

        val account = root.findViewById<Button>(R.id.account)
        val s1 = SpannableStringBuilder().bold { scale(1.2f) { append("Account") } }.append("\nSet profile preferences to improve recommendations")
        account.text = s1
        val jobs = root.findViewById<Button>(R.id.jobs)
        val s2 = SpannableStringBuilder().bold { scale(1.2f) { append("Jobs") } }.append("\nView jobs you've applied")
        jobs.text = s2
        val reviews = root.findViewById<Button>(R.id.reviews)
        val s3 = SpannableStringBuilder().bold { scale(1.2f) { append("Reviews") } }.append("\nSee reviews you've posted")
        reviews.text = s3

        // sign out
        val signOut = root.findViewById<Button>(R.id.signOut)
        signOut.setOnClickListener {
            viewModel.signOut()
            val authInitIntent = Intent(activity, MainActivity::class.java)
            startActivity(authInitIntent)
        }
        return root
    }
}
