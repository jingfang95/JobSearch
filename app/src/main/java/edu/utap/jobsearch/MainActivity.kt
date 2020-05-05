package edu.utap.jobsearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private fun initUserUI() {
        viewModel.observeFirebaseAuthLiveData().observe(this, Observer {
            if( it == null ) {
                Log.d(javaClass.simpleName, "No one is signed in")
            } else {
                Log.d(javaClass.simpleName, "${it.displayName} ${it.email} ${it.uid} signed in")
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUserUI()
        val authInitIntent = Intent(this, AuthInitActivity::class.java)
        startActivity(authInitIntent)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_saved, R.id.navigation_review, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }
}
