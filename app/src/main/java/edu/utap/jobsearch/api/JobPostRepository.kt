package edu.utap.jobsearch.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class JobPostRepository(private val jobApi: JobApi) {
//    val gson = GsonBuilder().registerTypeAdapter(
//        SpannableString::class.java, JobApi.SpannableDeserializer()
//    ).create()

    suspend fun getPosts(): List<JobPost> {
//        val response = jobApi.getPosts()
//        var posts = mutableListOf<JobPost>()
//        response.enqueue(object : Callback<List<JobPost>> {
//            override fun onFailure(call: Call<List<JobPost>>, t: Throwable) {
//
//            }
//            override fun onResponse(call: Call<List<JobPost>>, response: Response<List<JobPost>>) {
//                posts = response.body() as MutableList<JobPost>
//            }
//        })
        return jobApi.getPosts()
    }

}