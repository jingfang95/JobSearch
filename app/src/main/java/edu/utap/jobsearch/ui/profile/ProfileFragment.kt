package edu.utap.jobsearch.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.bold
import androidx.core.text.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.jobsearch.MainActivity
import edu.utap.jobsearch.R
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.UserRow
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.photo
import kotlinx.android.synthetic.main.fragment_profile.username
import kotlinx.android.synthetic.main.profile_account.*
import java.util.jar.Manifest

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var pictureUUID: String? = null
    private lateinit var user : UserRow

    companion object {
        const val cameraRC = 10
    }

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
        val institution = root.findViewById<TextView>(R.id.university)

        // profile photo
        val photo = root.findViewById<ImageView>(R.id.photo)
        db.collection("user")
            .whereEqualTo("ownerUid", viewModel.myUid())
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    return@addSnapshotListener
                }
                val result = querySnapshot!!.documents.mapNotNull {
                    it.toObject(UserRow::class.java)
                }
                if (!result.isNullOrEmpty()) {
                    user = result[0]
                    if (!user.pictureUUID.isNullOrEmpty()) {
                        viewModel.glideFetch(user.pictureUUID.toString(), photo)
                        pictureUUID = user.pictureUUID.toString()
                    }
                    if (!user.institution.isNullOrEmpty()) {
                        institution.text = user.institution.toString()
                    }
                }
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

        account.setOnClickListener {
            val intent = Intent(activity, AccountManager::class.java)
            intent.putExtra("username", username.text)
            intent.putExtra("ownerUid", viewModel.myUid())
            intent.putExtra("pictureUUID", pictureUUID)
            startActivity(intent)
        }

        jobs.setOnClickListener {
            val intent = Intent(activity, JobManager::class.java)
            intent.putExtra("ownerUid", viewModel.myUid())
            startActivity(intent)
        }

        reviews.setOnClickListener {
            val intent = Intent(activity, ReviewManager::class.java)
            intent.putExtra("ownerUid", viewModel.myUid())
            startActivity(intent)
        }

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
