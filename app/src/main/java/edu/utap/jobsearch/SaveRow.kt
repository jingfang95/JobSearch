package edu.utap.jobsearch

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class SaveRow(
    var id: String? = null,
    var company: String? = null,
    var ownerUid: String? = null,
    var location: String? = null,
    var title: String? = null,
    var company_logo: String? = null,
    var apply_url: String? = null,
    @ServerTimestamp val timeStamp: Timestamp? = null,
    var rowID: String = ""
)
