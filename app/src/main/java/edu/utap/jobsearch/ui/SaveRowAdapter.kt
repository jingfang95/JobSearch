package edu.utap.jobsearch.ui

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.text.Html
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
import edu.utap.jobsearch.*
import edu.utap.jobsearch.glide.Glide
import java.security.AccessController.getContext
import java.text.SimpleDateFormat

class SaveRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<SaveRow, SaveRowAdapter.VH>(JobDiff()) {
    class JobDiff : DiffUtil.ItemCallback<SaveRow>() {
        override fun areItemsTheSame(oldItem: SaveRow, newItem: SaveRow): Boolean {
            return oldItem.ownerUid == newItem.ownerUid
        }

        override fun areContentsTheSame(oldItem: SaveRow, newItem: SaveRow): Boolean {
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
        private var apply = itemView.findViewById<TextView>(R.id.apply)
        init {
//            companyCard.setOnClickListener {
//                Log.d("tag", "title has been clicked")
//                val position = adapterPosition
//                MainViewModel.doOnePost(it.context, getItem(position))
//            }
            apply.isClickable = true
            apply.setOnClickListener {
                val position = adapterPosition
                val applyIntent = Intent(Intent.ACTION_VIEW)
                applyIntent.data = Uri.parse(Html.fromHtml(getItem(position).apply_url).toString())
                val resultCode = 1
                itemView.context.startActivity(applyIntent)
            }
        }

        fun bind(item: SaveRow) {
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
            apply.text = Html.fromHtml(item.apply_url).toString()
            apply.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            if (Html.fromHtml(item.apply_url).length > 30) {
                apply.text = Html.fromHtml(item.apply_url).substring(0, 30) + "..."
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_saved, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

}