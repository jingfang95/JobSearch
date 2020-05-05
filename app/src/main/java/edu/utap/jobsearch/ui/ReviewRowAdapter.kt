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
import edu.utap.jobsearch.glide.Glide
import edu.utap.jobsearch.R
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.ReviewRow

class ReviewRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<ReviewRow, ReviewRowAdapter.VH>(JobDiff()) {
    class JobDiff : DiffUtil.ItemCallback<ReviewRow>() {
        override fun areItemsTheSame(oldItem: ReviewRow, newItem: ReviewRow): Boolean {
            return oldItem.ownerUid == newItem.ownerUid
        }

        override fun areContentsTheSame(oldItem: ReviewRow, newItem: ReviewRow): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.type == oldItem.type
                    && oldItem.title == oldItem.title
                    && oldItem.description == oldItem.description
        }
    }

    inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
//        private var companyCard = itemView.findViewById<LinearLayout>(R.id.companyCard)
        private var title = itemView.findViewById<TextView>(R.id.title)
        private var company = itemView.findViewById<TextView>(R.id.company)
//        private var rating = itemView.findViewById<TextView>(R.id.rating)
        private var description = itemView.findViewById<TextView>(R.id.description)
        init {
//            companyCard.setOnClickListener {
//                Log.d("tag", "title has been clicked")
//                val position = adapterPosition
//                MainViewModel.doOnePost(it.context, getItem(position))
//            }
        }

        fun bind(item: ReviewRow) {
            if (item == null) return
            title.text = item.title
            company.text = item.name
//            if (!item.rating.isNullOrEmpty()) {
//                rating.text = "Rating: ${item.rating}"
//            }
            description.text = item.description

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_review, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

}