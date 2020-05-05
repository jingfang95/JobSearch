package edu.utap.jobsearch.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ReviewRow
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.ui.ReviewRowAdapter

class CompanyReviewFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var review = MutableLiveData<List<ReviewRow>>()
    private lateinit var adapter: ReviewRowAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the root view and cache references to vital UI elements
        val root = inflater.inflate(R.layout.job_review, container, false)
        initAdapter(root)
        val cname = arguments?.getString("name")
        db.collection("review")
            .whereEqualTo("name", cname)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    review.value = querySnapshot.documents.mapNotNull {
                        it.toObject(ReviewRow::class.java)
                    }
                }
            }
        observeData().observe(viewLifecycleOwner,
            Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })

        return root
    }

    fun observeData(): LiveData<List<ReviewRow>> {
        return review
    }

    private fun initAdapter(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(context)
        adapter = ReviewRowAdapter(viewModel)
        rv.adapter = adapter
    }

}
