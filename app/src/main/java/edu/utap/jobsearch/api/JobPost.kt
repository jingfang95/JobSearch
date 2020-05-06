package edu.utap.jobsearch.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import com.google.gson.annotations.SerializedName

data class JobPost (
    @SerializedName("id")
    val key: String,
    @SerializedName("type")
    val type: SpannableString?,
    @SerializedName("created_at")
    val date: SpannableString?,
    @SerializedName("company")
    val company: SpannableString?,
    @SerializedName("company_url")
    val company_url: String,
    @SerializedName("location")
    val location: SpannableString?,
    @SerializedName("title")
    val title: SpannableString?,
    @SerializedName("description")
    val description: SpannableString?,
    @SerializedName("how_to_apply")
    val apply_url: String,
    @SerializedName("company_logo")
    val company_logo: String
) {
    companion object {
        private fun setSpan(fulltext: SpannableString, subtext: String): Boolean {
            if (subtext.isEmpty()) return true
            val i = fulltext.indexOf(subtext, ignoreCase = true)
            if (i == -1) return false
            fulltext.setSpan(
                ForegroundColorSpan(Color.BLUE), i, i + subtext.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return true
        }
    }

    private fun removeAllCurrentSpans() {
        company?.clearSpans()
        location?.clearSpans()
        title?.clearSpans()
    }

    fun searchFor(searchTerm: String): Boolean {
        var isFound = false
        if (company != null && setSpan(company, searchTerm)) {
            isFound = true
        }
        if (location != null && setSpan(location, searchTerm)) {
            isFound = true
        }
        if (title != null && setSpan(title, searchTerm)) {
            isFound = true
        }
        removeAllCurrentSpans()
        return isFound
    }

    fun searchForLocation(searchTerm: String): Boolean {
        var isFound = false
        if (location != null && setSpan(location, searchTerm)) {
            isFound = true
        }
        removeAllCurrentSpans()
        return isFound
    }

    fun searchForType(searchTerm: String): Boolean {
        var isFound = false
        if (type != null && setSpan(type, searchTerm)) {
            isFound = true
        }
        removeAllCurrentSpans()
        return isFound
    }

    override fun equals(other: Any?): Boolean =
        if (other is JobPost) {
            key == other.key
        } else {
            false
        }
}
