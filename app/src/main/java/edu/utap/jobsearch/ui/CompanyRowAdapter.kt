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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.jobsearch.glide.Glide
import edu.utap.jobsearch.R
import edu.utap.jobsearch.api.JobPost
import okhttp3.internal.UTC
import java.text.SimpleDateFormat
import java.util.*
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.SaveRow

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
//        private var date = itemView.findViewById<TextView>(R.id.date)
        private var companyLogo = itemView.findViewById<ImageView>(R.id.companyLogo)
        private var rowFav = itemView.findViewById<ImageView>(R.id.rowFav)
        private val ownerID = viewModel.myUid()

        private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        private lateinit var savedJob : SaveRow
        init {
            companyCard.setOnClickListener {
                Log.d("tag", "title has been clicked")
                val position = adapterPosition
                MainViewModel.doOnePost(it.context, getItem(position), ownerID)
            }

            rowFav.setOnClickListener {
                val position = adapterPosition

//                if (viewModel.isFav(getItem(position))) {
//                    viewModel.removeFav(getItem(position))
//                    // delete
//                    db.collection("save").document(savedJob.rowID).delete()
//                        .addOnSuccessListener {
//                            Log.d(javaClass.simpleName, "Deleted ${savedJob.rowID}")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.d(javaClass.simpleName, "Delete FAILED of ${savedJob.rowID}")
//                            Log.w(javaClass.simpleName, "Error deleting document", e)
//                        }
//
//                } else {
//                    viewModel.addFav(getItem(position))
//                    // add
//                    savedJob = SaveRow().apply {
//                        id = getItem(position).key
//                    }
//                    savedJob.rowID = db.collection("save").document().id
//                    val jobInfo = hashMapOf(
//                        "id" to getItem(position).key,
////                        "type" to getItem(position).type,
////                        "created_at" to getItem(position).date,
////                        "company" to getItem(position).company,
//                        "ownerUid" to ownerID,
////                        "company_url" to getItem(position).company_url,
////                        "location" to getItem(position).location,
////                        "title" to getItem(position).title,
////                        "company_logo" to getItem(position).company_logo,
////                        "description" to getItem(position).description,
////                        "apply_url" to getItem(position).apply_url,
//                        "timeStamp" to Timestamp(Date()),
//                        "rowID" to savedJob.rowID
//                    )
//                    db.collection("save").document(savedJob.rowID)
//                        .set(jobInfo)
//                        .addOnSuccessListener {
//                            Log.d(javaClass.simpleName, "Deleted ${savedJob.rowID}")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.d(javaClass.simpleName, "Delete FAILED of ${savedJob.rowID}")
//                            Log.w(javaClass.simpleName, "Error deleting document", e)
//                        }
//                }
//                notifyItemChanged(position)
            }
        }

        fun bind(item: JobPost?) {
            // do one company
            if (item == null) return
            title.text = item.title
            if (item.title?.length ?: 0 > 20) {
                title.text = item.title?.substring(0, 20) + "..."
            }
            company.text = item.company
            location.text = item.location
            // Fri Apr 24 20:44:12 UTC 2020
//            val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
//            val postDate = formatter.parse(item.date.toString())
//            val current = getRelativeDateTimeString(this.itemView.context, postDate, Calendar.getInstance(UTC).timeInMillis, DAY_IN_MILLIS,0)
//            date.text = item.date

            // set image
            if (!item.company_logo.isNullOrEmpty()) {
                Glide.glideFetch(item.company_logo, companyLogo)
            }
//            if (viewModel.isFav(item)) {
//                rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
//            } else {
//                rowFav.setImageResource(R.drawable.ic_favorite_border_24dp)
//            }
            db.collection("save")
                .whereEqualTo("ownerUid", ownerID)
                .whereEqualTo("id", item.key)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        return@addSnapshotListener
                    }
                    val result = querySnapshot!!.documents.mapNotNull {
                        it.toObject(SaveRow::class.java)
                    }
                    if (!result.isNullOrEmpty()) {
                        Log.d("Find job", "saved job")
                        savedJob = result[0]
                        rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
                        rowFav.setOnClickListener {
                            db.collection("save").document(savedJob.rowID).delete()
                                .addOnSuccessListener {
                                    Log.d(javaClass.simpleName, "Deleted ${savedJob.rowID}")
                                }
                                .addOnFailureListener { e ->
                                    Log.d(javaClass.simpleName, "Delete FAILED of ${savedJob.rowID}")
                                    Log.w(javaClass.simpleName, "Error deleting document", e)
                                }
                            rowFav.setImageResource(R.drawable.ic_favorite_border_24dp)
                        }
                    } else {
                        rowFav.setImageResource(R.drawable.ic_favorite_border_24dp)
                        rowFav.setOnClickListener {
                            savedJob = SaveRow().apply {
                                id = item.key
                            }
                            savedJob.rowID = db.collection("save").document().id
                            val jobInfo = hashMapOf(
                                "id" to item.key,
                                "company" to item.company.toString(),
                                "ownerUid" to ownerID,
                                "location" to item.location.toString(),
                                "title" to item.title.toString(),
                                "company_logo" to item.company_logo,
                                "apply_url" to item.apply_url,
                                "timeStamp" to Timestamp(Date()),
                                "rowID" to savedJob.rowID
                            )
                            db.collection("save").document(savedJob.rowID)
                                .set(jobInfo)
                                .addOnSuccessListener {
                                    Log.d(javaClass.simpleName, "Deleted ${savedJob.rowID}")
                                }
                                .addOnFailureListener { e ->
                                    Log.d(javaClass.simpleName, "Delete FAILED of ${savedJob.rowID}")
                                    Log.w(javaClass.simpleName, "Error deleting document", e)
                                }
                            rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
                        }

                    }
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