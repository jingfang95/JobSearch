package edu.utap.jobsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.jobsearch.R
import edu.utap.jobsearch.api.JobApi
import edu.utap.jobsearch.api.JobPost
import edu.utap.jobsearch.api.JobPostRepository
import edu.utap.jobsearch.ui.home.OneCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var searchTerm = MutableLiveData<String>()
    private val api = JobApi.create()
    private val repository = JobPostRepository(api)
    private val data = MutableLiveData<List<JobPost>>()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }

    // fav
    private var favPosts = MutableLiveData<List<JobPost>>().apply {
        value = mutableListOf()
    }

    fun netPosts() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ){
            data.postValue(repository.getPosts())
        }
    }

    fun searchPosts() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ){
            val jobPosts = repository.getPosts()
            val searchResult = mutableListOf<JobPost>()
            for (post in jobPosts) {
                if (post.searchFor(searchTerm.value.toString())) {
                    searchResult.add(post)
                }
            }
            data.postValue(searchResult)
        }
    }

    fun observeData(): LiveData<List<JobPost>> {
        return data
    }

    fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }

    fun myDisplayName(): String? {
        return firebaseAuthLiveData.value?.displayName
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    // fav
    fun observeFav(): LiveData<List<JobPost>> {
        return favPosts
    }

    fun addFav(postRec: JobPost) {
        val localList = favPosts.value?.toMutableList()
        localList?.let {
            it.add(postRec)
            favPosts.value = it
        }
    }

    fun removeFav(postRec: JobPost) {
        val localList = favPosts.value?.toMutableList()
        localList?.let {
            it.remove(postRec)
            favPosts.value = it
        }
    }

    fun isFav(postRec: JobPost): Boolean {
        return favPosts.value?.contains(postRec)?: false
    }

    companion object {
        fun doOnePost(context: Context, jobPost: JobPost) {
            val onePostIntent = Intent(context, OneCompany::class.java)
            onePostIntent.apply {
                putExtra("postCompany", jobPost.company.toString())
                putExtra("postTitle", jobPost.title.toString())
                putExtra("postLocation", jobPost.location.toString())
                putExtra("postDate", jobPost.date.toString())
                putExtra("postType", jobPost.type.toString())
                putExtra("postDescription", jobPost.description.toString())
                putExtra("companyURL", jobPost.company_url)
                putExtra("applyURL", jobPost.apply_url)
                putExtra("companyLogo", jobPost.company_logo)
            }
            context.startActivity(onePostIntent)
        }
    }

}