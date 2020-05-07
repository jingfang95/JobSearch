package edu.utap.jobsearch.ui.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.SaveRow
import edu.utap.jobsearch.ui.SaveRowAdapter

class SavedFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: SaveRowAdapter
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var job = MutableLiveData<List<SaveRow>>()

    companion object {
        fun newInstance(): SavedFragment {
            return SavedFragment()
        }
    }

    private fun initRecyclerView(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = SaveRowAdapter(viewModel)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_saved, container, false)
        initRecyclerView(root)
//        viewModel.initFav(viewModel.myUid().toString())
//        viewModel.observeFav().observe(viewLifecycleOwner,
//            Observer {
//                adapter.submitList(it)
//                adapter.notifyDataSetChanged()
//            })
        db.collection("save")
            .whereEqualTo("ownerUid", viewModel.myUid())
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.d("checked", String.format("id is null"))
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    job.value = querySnapshot.documents.mapNotNull {
                        Log.d("checked", String.format("not null"))
                        it.toObject(SaveRow::class.java)
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

    fun observeData(): LiveData<List<SaveRow>> {
        return job
    }
}
