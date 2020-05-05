package edu.utap.jobsearch

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ReviewRow(
    var name: String? = null,
    var title: String? = null,
    var rating: String? = null,
    var type: String? = null,
    var ownerUid: String? = null,
    var description: String? = null,
    var comment: String? = null,
    @ServerTimestamp val timeStamp: Timestamp? = null,
    var rowID: String = ""
)
