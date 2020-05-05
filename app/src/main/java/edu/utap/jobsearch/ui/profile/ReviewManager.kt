package edu.utap.jobsearch.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ReviewRow
import edu.utap.jobsearch.UserRow
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.ui.ReviewRowAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_account.*
import kotlinx.android.synthetic.main.profile_account.photo

class ReviewManager : AppCompatActivity() {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ReviewRowAdapter
    private var review = MutableLiveData<List<ReviewRow>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_review)

        val activityThatCalled = intent
        val ownerUid = activityThatCalled.extras?.getString("ownerUid")
        Log.d("checked", String.format("id is: %s", ownerUid))
        initRecyclerView()
        db.collection("review")
            .whereEqualTo("ownerUid", ownerUid)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.d("checked", String.format("id is null"))
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    review.value = querySnapshot.documents.mapNotNull {
                        Log.d("checked", String.format("not null"))
                        it.toObject(ReviewRow::class.java)
                    }
                }
            }
        observeData().observe(this,
            Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })
    }

    fun observeData(): LiveData<List<ReviewRow>> {
        return review
    }

    private fun initRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ReviewRowAdapter(viewModel)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}