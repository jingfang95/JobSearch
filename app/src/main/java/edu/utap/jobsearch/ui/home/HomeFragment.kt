package edu.utap.jobsearch.ui.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.MainViewModel

class HomeFragment : Fragment() {
    private lateinit var adapter: CompanyRowAdapter
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun initAdapter(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(context)
        adapter = CompanyRowAdapter(viewModel)
        rv.adapter = adapter
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        initAdapter(root)
        val actionSearch = root.findViewById<EditText>(R.id.actionSearch)
        viewModel.searchTerm.apply {
            value = ""
        }
        actionSearch?.setOnEditorActionListener { _, i, keyEvent ->
            if ( (keyEvent != null
                && (keyEvent.action == KeyEvent.ACTION_DOWN)
                && (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) || i == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                viewModel.searchTerm.apply {
                    value = actionSearch.text.toString()
                }
                viewModel.searchPosts()

            }
            false
        }
        viewModel.netPosts()
        viewModel.observeData().observe(viewLifecycleOwner,
            Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })

        return root
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.window?.decorView?.rootView?.windowToken, 0);
    }
}
