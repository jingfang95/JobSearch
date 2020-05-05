package edu.utap.jobsearch.ui

import android.os.Bundle
import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.getRelativeDateTimeString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import edu.utap.jobsearch.glide.Glide
import edu.utap.jobsearch.R
import edu.utap.jobsearch.api.JobPost
import okhttp3.internal.UTC
import java.text.SimpleDateFormat
import java.util.*

class CompanyRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<JobPost, CompanyRowAdapter.VH>(JobDiff()) {
    class JobDiff : DiffUtil.ItemCallback<JobPost>() {
        override fun areItemsTheSame(oldItem: JobPost, newItem: JobPost): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: JobPost, newItem: JobPost): Boolean {
            return oldItem.company == newItem.company
                    && oldItem.location == oldItem.location
                    && oldItem.title == oldItem.title
        }
    }

    inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        private var companyCard = itemView.findViewById<LinearLayout>(R.id.companyCard)
        private var title = itemView.findViewById<TextView>(R.id.title)
        private var company = itemView.findViewById<TextView>(R.id.company)
        private var location = itemView.findViewById<TextView>(R.id.location)
        private var date = itemView.findViewById<TextView>(R.id.date)
        private var companyLogo = itemView.findViewById<ImageView>(R.id.companyLogo)
        private var rowFav = itemView.findViewById<ImageView>(R.id.rowFav)
        init {
            companyCard.setOnClickListener {
                Log.d("tag", "title has been clicked")
                val position = adapterPosition
                MainViewModel.doOnePost(it.context, getItem(position))
            }

            rowFav.setOnClickListener {
                val position = adapterPosition

                if (viewModel.isFav(getItem(position))) {
                    viewModel.removeFav(getItem(position))
                } else {
                    viewModel.addFav(getItem(position))
                }
                notifyItemChanged(position)
            }
        }

        fun bind(item: JobPost?) {
            // do one company
            if (item == null) return
            title.text = item.title
            company.text = item.company
            location.text = item.location
            // Fri Apr 24 20:44:12 UTC 2020
//            val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
//            val postDate = formatter.parse(item.date.toString())
//            val current = getRelativeDateTimeString(this.itemView.context, postDate, Calendar.getInstance(UTC).timeInMillis, DAY_IN_MILLIS,0)
            date.text = item.date

            // set image
            if (!item.company_logo.isNullOrEmpty()) {
                Glide.glideFetch(item.company_logo, companyLogo)
            }
            if (viewModel.isFav(item)) {
                rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                rowFav.setImageResource(R.drawable.ic_favorite_border_24dp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_company, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

}