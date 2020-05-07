package edu.utap.jobsearch.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import edu.utap.jobsearch.*
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.ui.JobRowAdapter
import edu.utap.jobsearch.ui.ReviewRowAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_account.*
import kotlinx.android.synthetic.main.profile_account.photo

class JobManager : AppCompatActivity() {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: JobRowAdapter
    private var job = MutableLiveData<List<JobRow>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_job)
        val activityThatCalled = intent
        val ownerUid = activityThatCalled.extras?.getString("ownerUid")
        initRecyclerView()
        db.collection("job")
            .whereEqualTo("ownerUid", ownerUid)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.d("checked", String.format("id is null"))
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    job.value = querySnapshot.documents.mapNotNull {
                        Log.d("checked", String.format("not null"))
                        it.toObject(JobRow::class.java)
                    }
                }
            }
        observeData().observe(this,
            Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })
    }

    fun observeData(): LiveData<List<JobRow>> {
        return job
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = JobRowAdapter(viewModel)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
    }
}