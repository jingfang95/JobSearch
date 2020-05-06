package edu.utap.jobsearch.ui.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.jobsearch.R
import edu.utap.jobsearch.ui.CompanyRowAdapter
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.api.JobPost
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var adapter: CompanyRowAdapter
    private val viewModel: MainViewModel by activityViewModels()

    private val locations: Array<String> by lazy {
        resources.getStringArray(R.array.location)
    }

    private val jobTypes: Array<String> by lazy {
        resources.getStringArray(R.array.job_type)
    }

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
        val filterSearch = root.findViewById<ImageButton>(R.id.filterSearch)
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
        filterSearch.setOnClickListener {
            val view = inflater.inflate(R.layout.filter_window, null)
            val filterWindow = PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            filterWindow.elevation = 10.0f

            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            filterWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.TOP
            filterWindow.exitTransition = slideOut

            val locationAdapter = ArrayAdapter.createFromResource(view.context,
                R.array.location,
                android.R.layout.simple_spinner_item)
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val locationSpinner = view.findViewById<Spinner>(R.id.location_spinner)
            locationSpinner.adapter = locationAdapter

            val jobTypeAdapter = ArrayAdapter.createFromResource(view.context,
                R.array.job_type,
                android.R.layout.simple_spinner_item)
            jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val typeSpinner = view.findViewById<Spinner>(R.id.type_spinner)
            typeSpinner.adapter = jobTypeAdapter

            val cancelBut = view.findViewById<Button>(R.id.cancel)
            cancelBut.setOnClickListener {
                filterWindow.dismiss()
            }

            val goBut = view.findViewById<Button>(R.id.search)
            goBut.setOnClickListener {
                var handleLocation = ""
                val locationPos = locationSpinner.selectedItemPosition
                if (locationPos != 0) {
                    handleLocation = locations[locationPos]
                }
                var handleType = ""
                val typePos = typeSpinner.selectedItemPosition
                if (typePos != 0) {
                    handleType = jobTypes[typePos]
                }
                viewModel.searchLocation.apply {
                    value = handleLocation
                }
                viewModel.searchType.apply {
                    value = handleType
                }
                viewModel.searchPostsForFilter()
                filterWindow.dismiss()
            }
            TransitionManager.beginDelayedTransition(home_layout)
            filterWindow.showAtLocation(home_layout, Gravity.TOP, 0, 100)

        }
        viewModel.netPosts()
        viewModel.observeData().observe(viewLifecycleOwner,
            Observer {
                if (it.isNullOrEmpty()) {
                    Toast.makeText(context, "No Result Found", Toast.LENGTH_SHORT).show()
                }
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
