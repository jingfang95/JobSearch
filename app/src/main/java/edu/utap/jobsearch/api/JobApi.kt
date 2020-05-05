package edu.utap.jobsearch.api

import android.text.SpannableString
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface JobApi {
    @GET("/positions.json")
    suspend fun getPosts(): List<JobPost>

//    data class ListingResponse(val data: List<JobPost>)

    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    // https://jobs.github.com/positions.json
    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder().registerTypeAdapter(
                SpannableString::class.java, SpannableDeserializer()
            )
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("jobs.github.com")
            .build()
        fun create(): JobApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): JobApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(JobApi::class.java)
        }
    }

}