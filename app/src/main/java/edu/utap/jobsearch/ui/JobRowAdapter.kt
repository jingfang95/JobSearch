package edu.utap.jobsearch.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import edu.utap.jobsearch.JobRow
import edu.utap.jobsearch.glide.Glide
import edu.utap.jobsearch.R
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.ReviewRow
import java.text.SimpleDateFormat

class JobRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<JobRow, JobRowAdapter.VH>(JobDiff()) {
    class JobDiff : DiffUtil.ItemCallback<JobRow>() {
        override fun areItemsTheSame(oldItem: JobRow, newItem: JobRow): Boolean {
            return oldItem.ownerUid == newItem.ownerUid
        }

        override fun areContentsTheSame(oldItem: JobRow, newItem: JobRow): Boolean {
            return oldItem.company == newItem.company
                    && oldItem.company_logo == newItem.company_logo
                    && oldItem.title == newItem.title
                    && oldItem.location == newItem.location
                    && oldItem.id == newItem.id
        }
    }

    inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
//        private var companyCard = itemView.findViewById<LinearLayout>(R.id.companyCard)
        private var logo = itemView.findViewById<ImageView>(R.id.companyLogo)
        private var title = itemView.findViewById<TextView>(R.id.title)
        private var company = itemView.findViewById<TextView>(R.id.company)
//        private var rating = itemView.findViewById<TextView>(R.id.rating)
        private var location = itemView.findViewById<TextView>(R.id.location)
        private var date = itemView.findViewById<TextView>(R.id.time)
        init {
//            companyCard.setOnClickListener {
//                Log.d("tag", "title has been clicked")
//                val position = adapterPosition
//                MainViewModel.doOnePost(it.context, getItem(position))
//            }
        }

        fun bind(item: JobRow) {
            if (item == null) return
            if (!item.company_logo.isNullOrEmpty()) {
                Glide.glideFetch(item.company_logo!!, logo)
            }
            title.text = item.title
            if (item.title?.length ?: 0 > 20) {
                title.text = item.title?.substring(0, 20) + "..."
            }
            company.text = item.company
//            if (!item.rating.isNullOrEmpty()) {
//                rating.text = "Rating: ${item.rating}"
//            }
            location.text = item.location
            val timeList = item.timeStamp?.toDate().toString().split(" ")
            if (timeList.size >= 6) {
                date.text = timeList[1] + " " + timeList[2] + ", " + timeList[5]
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_applied, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

}