package edu.utap.jobsearch

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.jobsearch.R
import edu.utap.jobsearch.api.JobApi
import edu.utap.jobsearch.api.JobPost
import edu.utap.jobsearch.api.JobPostRepository
import edu.utap.jobsearch.glide.Glide
import edu.utap.jobsearch.ui.home.OneCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

class MainViewModel(application: Application, private val state: SavedStateHandle)
    : AndroidViewModel(application) {

    private val appContext = getApplication<Application>().applicationContext
    private var storageDir =
        getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    var searchTerm = MutableLiveData<String>()
    var searchLocation = MutableLiveData<String>()
    var searchType = MutableLiveData<String>()
    private val api = JobApi.create()
    private val repository = JobPostRepository(api)
    private val data = MutableLiveData<List<JobPost>>()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val pictureUUIDKey = "pictureUUIDKey"
    // NB: Here is a problem with the way I do pictures.  It works when I use
    // local variables to save these "function pointers."  But the viewModel can be
    // cleared, so we want to save these function pointers.  But they are actually closures
    // with a reference to the activity/fragment that created them.  So we get a
    // parcelable error if we try to store them into a SavedStateHandle
    private val photoSuccessKey = "photoSuccessKey"
    private val takePhotoIntentKey = "takePhotoIntentKey"

    private lateinit var crashMe: String
    private fun noPhoto() {
        Log.d(javaClass.simpleName, "Function must be initialized to something that can start the camera intent")
        crashMe.plus(" ")
    }
    private var takePhotoIntent: () -> Unit = ::noPhoto
    private fun defaultPhoto(@Suppress("UNUSED_PARAMETER") path: String) {
        Log.d(javaClass.simpleName, "Function must be initialized to photo callback" )
        crashMe.plus(" ")
    }
    private var photoSuccess: (path: String) -> Unit = ::defaultPhoto

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
            val getResult = mutableListOf<JobPost>()
            var jobPosts = repository.getPosts()
            for (post in jobPosts) {
                getResult.add(post)
            }
            jobPosts = repository.getPostsPage2()
            for (post in jobPosts) {
                getResult.add(post)
            }

            jobPosts = repository.getPostsPage3()
            for (post in jobPosts) {
                getResult.add(post)
            }

            jobPosts = repository.getPostsPage4()
            for (post in jobPosts) {
                getResult.add(post)
            }

            data.postValue(getResult)
        }
    }

    fun searchPosts() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ){
            var jobPosts = repository.getPosts()
            val searchResult = mutableListOf<JobPost>()
            for (post in jobPosts) {
                if (post.searchFor(searchTerm.value.toString())) {
                    searchResult.add(post)
                }
            }

            jobPosts = repository.getPostsPage2()
            for (post in jobPosts) {
                if (post.searchFor(searchTerm.value.toString())) {
                    searchResult.add(post)
                }
            }

            jobPosts = repository.getPostsPage3()
            for (post in jobPosts) {
                if (post.searchFor(searchTerm.value.toString())) {
                    searchResult.add(post)
                }
            }

            jobPosts = repository.getPostsPage4()
            for (post in jobPosts) {
                if (post.searchFor(searchTerm.value.toString())) {
                    searchResult.add(post)
                }
            }

            data.postValue(searchResult)
        }
    }

    fun searchPostsForFilter() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ){
            var jobPosts = repository.getPosts()
            val searchResult = mutableListOf<JobPost>()
            for (post in jobPosts) {
                if (post.searchForLocation(searchLocation.value.toString()) && post.searchForType(searchType.value.toString())) {
                    searchResult.add(post)
                }
            }

            jobPosts = repository.getPostsPage2()
            for (post in jobPosts) {
                if (post.searchForLocation(searchLocation.value.toString()) && post.searchForType(searchType.value.toString())) {
                    searchResult.add(post)
                }
            }

            jobPosts = repository.getPostsPage3()
            for (post in jobPosts) {
                if (post.searchForLocation(searchLocation.value.toString()) && post.searchForType(searchType.value.toString())) {
                    searchResult.add(post)
                }
            }

            jobPosts = repository.getPostsPage4()
            for (post in jobPosts) {
                if (post.searchForLocation(searchLocation.value.toString()) && post.searchForType(searchType.value.toString())) {
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

    // photo
    fun setPhotoIntent(_takePhotoIntent: () -> Unit) {
        takePhotoIntent = _takePhotoIntent
        //state.set(takePhotoIntentKey, takePhotoIntent)
    }

    fun takePhoto(_photoSuccess: (String) -> Unit) {
        photoSuccess = _photoSuccess
        //state.set(photoSuccessKey, photoSuccess)
        takePhotoIntent.invoke()
    }

    fun getPhotoURI(): Uri {
        // Create an image file name
        state.set(pictureUUIDKey,  UUID.randomUUID().toString())
        Log.d(javaClass.simpleName, "pictureUUID ${state.get<String>(pictureUUIDKey)}")
        var photoUri: Uri? = null
        // Create the File where the photo should go
        try {
            val localPhotoFile = File(storageDir, "${state.get<String>(pictureUUIDKey)}.jpg")
            Log.d(javaClass.simpleName, "photo path ${localPhotoFile.absolutePath}")
            photoUri = FileProvider.getUriForFile(
                appContext,
                "edu.utap.jobsearch",
                localPhotoFile
            )
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.d(javaClass.simpleName, "Cannot create file", ex)
        }
        // CRASH.  Production code should do something more graceful
        return photoUri!!
    }
    /////////////////////////////////////////////////////////////
    // Callbacks from MainActivity.getResultForActivity from camera intent
    // We can't just schedule the file upload and return.
    // The problem is that our previous picture uploads can still be pending.
    // So a note can have a pictureUUID that does not refer to an existing file.
    // That violates referential integrity, which we really like in our db (and programming
    // model).
    // So we do not add the pictureUUID to the note until the picture finishes uploading.
    // That means a user won't see their picture updates immediately, they have to
    // wait for some interaction with the server.
    // You could imagine dealing with this somehow using local files while waiting for
    // a server interaction, but that seems error prone.
    // Freezing the app during an upload also seems bad.
    fun pictureSuccess() {
        val pictureUUID = state.get(pictureUUIDKey) ?: ""
        val localPhotoFile = File(storageDir, "${pictureUUID}.jpg")
        Log.d(javaClass.simpleName, "pictureSuccess ${localPhotoFile.absolutePath}")
        // Wait until photo is successfully uploaded before calling back
        Storage.uploadImage(localPhotoFile, pictureUUID) {
            Log.d(javaClass.simpleName, "uploadImage callback ${pictureUUID}")
            photoSuccess(pictureUUID)
            photoSuccess = ::defaultPhoto
//            state.get<(String)->Unit>(photoSuccessKey)?.invoke(pictureUUID)
//            state.set(photoSuccessKey, ::defaultPhoto)
            state.set(pictureUUIDKey, "")
        }
    }
    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        state.set(pictureUUIDKey, "")
        Log.d(javaClass.simpleName, "pictureFailure pictureUUID cleared")
    }

    fun glideFetch(pictureUUID: String, imageView: ImageView) {
        // NB: Should get apsect ratio from image itself
        Glide.fetch(Storage.uuid2StorageReference(pictureUUID), imageView)
    }

    companion object {
        fun doOnePost(context: Context, jobPost: JobPost, ownerID: String?) {
            val onePostIntent = Intent(context, OneCompany::class.java)
            onePostIntent.apply {
                putExtra("ownerID", ownerID.toString())
                putExtra("postID", jobPost.key)
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