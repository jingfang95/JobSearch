package edu.utap.jobsearch.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.MainViewModel

class SavedFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: CompanyRowAdapter

    companion object {
        fun newInstance(): SavedFragment {
            return SavedFragment()
        }
    }

    private fun initRecyclerView(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CompanyRowAdapter(viewModel)
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
        viewModel.observeFav().observe(viewLifecycleOwner,
            Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })
        return root
    }
}
