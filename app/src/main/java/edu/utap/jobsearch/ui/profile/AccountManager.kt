package edu.utap.jobsearch.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.utap.jobsearch.MainViewModel
import edu.utap.jobsearch.R
import edu.utap.jobsearch.UserRow
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_account.*
import kotlinx.android.synthetic.main.profile_account.photo

class AccountManager : AppCompatActivity() {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var user : UserRow
    private var fragmentUUID: String? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_account)

        val username = findViewById<TextView>(R.id.username)
        val activityThatCalled = intent
        username.text = activityThatCalled.extras?.getString("username")
        val ownerUid = activityThatCalled.extras?.getString("ownerUid")
        val photo = findViewById<ImageView>(R.id.photo)
        viewModel.setPhotoIntent(::takePhotoIntent)
        photo.setOnClickListener {
            viewModel.takePhoto {
                Log.d(javaClass.simpleName, "uuid $it")
                fragmentUUID = it
                viewModel.glideFetch(fragmentUUID.toString(), photo)
            }
        }

        val save = findViewById<Button>(R.id.account_save)
        val institution = findViewById<EditText>(R.id.institution)
        val location = findViewById<EditText>(R.id.location)
        save.setOnClickListener {
            db.collection("user")
                .whereEqualTo("ownerUid", ownerUid)
                .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    return@addSnapshotListener
                }
                val result = querySnapshot!!.documents.mapNotNull {
                    it.toObject(UserRow::class.java)
                }
                if (!result.isNullOrEmpty()) {
                    user = result[0]
                    val userInfo = hashMapOf(
                        "name" to username.text,
                        "institution" to institution.text.toString(),
                        "location" to location.text.toString(),
                        "type" to type.text.toString(),
                        "ownerUid" to ownerUid,
                        "pictureUUID" to fragmentUUID,
                        "rowID" to user.rowID
                    )
                    db.collection("user").document(user.rowID)
                        .set(userInfo)
                        .addOnSuccessListener {
                            Log.d(javaClass.simpleName, "Updated ${user.rowID}")
                        }
                        .addOnFailureListener { e ->
                            Log.d(javaClass.simpleName, "Updated FAILED of ${user.rowID}")
                            Log.w(javaClass.simpleName, "Error updating document", e)
                        }

                } else {
                    user = UserRow().apply {
                        name = username.text.toString()
                    }
                    user.rowID = db.collection("user").document().id
                    val userInfo = hashMapOf(
                        "name" to username.text,
                        "institution" to institution.text.toString(),
                        "location" to location.text.toString(),
                        "type" to type.text.toString(),
                        "ownerUid" to ownerUid,
                        "pictureUUID" to fragmentUUID,
                        "rowID" to user.rowID
                    )
                    db.collection("user").document(user.rowID)
                        .set(userInfo)
                        .addOnSuccessListener {
                            Log.d(javaClass.simpleName, "Updated ${user.rowID}")
                        }
                        .addOnFailureListener { e ->
                            Log.d(javaClass.simpleName, "Updated FAILED of ${user.rowID}")
                            Log.w(javaClass.simpleName, "Error updating document", e)
                        }

                }
            }
            finish()
        }
    }

    private fun takePhotoIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePhotoIntent ->
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.getPhotoURI())
            startActivityForResult(takePhotoIntent, ProfileFragment.cameraRC)
        }
        Log.d(javaClass.simpleName, "takePhotoIntent")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(javaClass.simpleName, "onActivityResult")
        when (requestCode) {
            ProfileFragment.cameraRC -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.pictureSuccess()
                } else {
                    viewModel.pictureFailure()
                }
            }
        }
    }
}