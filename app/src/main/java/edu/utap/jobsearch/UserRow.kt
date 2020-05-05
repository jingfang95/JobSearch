package edu.utap.jobsearch

data class UserRow(
    var name: String? = null,
    var institution: String? = null,
    var location: String? = null,
    var type: String? = null,
    var ownerUid: String? = null,
    var pictureUUID: String? = null,
    var rowID: String = ""
)
