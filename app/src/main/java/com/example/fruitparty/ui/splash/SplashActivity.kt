package com.example.fruitparty.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.services.Constants
import com.example.fruitparty.data.services.Constants.TAG_TEST
import com.example.fruitparty.data.services.DataLoadingState
import com.example.fruitparty.databinding.ActivitySplashBinding
import com.example.fruitparty.ui.chooseGameActivity.ChooseGameActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val splashViewModel by viewModel<SplashViewModel>()
    private var doubleBackToExitPressedOnce = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        splashViewModel.apply {
            liveDataLoadingState.observe(this@SplashActivity) {
                Log.d(TAG_TEST, it.status.toString())
                when (it.status) {
                    DataLoadingState.Status.READING_FIREBASE -> {
                        binding.progressBar.isVisible = true
                    }
                    DataLoadingState.Status.READING_BLOGS_URL_FROM_FIREBASE -> {
                        binding.progressBar.isVisible = true
                    }
                    DataLoadingState.Status.LOADED_FIREBASE_MODEL -> {
                        addFireStoreModel(it.fireStoreModelWithUrl!!)
                        startChooseGameFragment()
                    }
                    DataLoadingState.Status.LOADED_BLOGS_URL_FROM_FIREBASE -> {
                        addFireStoreModel(FireStoreModelWithUrl(articleUrl = it.msg!!))
                        startChooseGameFragment()
                    }
                    DataLoadingState.Status.ERROR_INTERNET_CONNECTION, DataLoadingState.Status.ERROR_TIMEOUT_EXPIRED -> {
                        startChooseGameFragment()
                    }
                    DataLoadingState.Status.ERROR_FIREBASE_MODEL -> {
                        readUrlFromFirebase()
                    }
                    else -> {}
                }
            }
            subscribeFlowDataLoadingState()
            askNotificationPermission()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG_TEST, "askNotificationPermission: permission already granted")
                splashViewModel.readFireBase()
            } else {
                Log.d(TAG_TEST, "askNotificationPermission: launch permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d(TAG_TEST, "askNotificationPermission: Post Notification is not required")
            splashViewModel.readFireBase()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) Log.d(TAG_TEST, "Post Notification Permission is granted")
        else Log.d(TAG_TEST, "Post Notification Permission is denied")
        splashViewModel.readFireBase()
    }

    private fun startChooseGameFragment() {
        val chooseGameActivityIntent = Intent(this@SplashActivity, ChooseGameActivity::class.java)
        startActivity(chooseGameActivityIntent)
        finish()
    }

    private fun showError() {
        binding.progressBar.isVisible = false
        Snackbar.make(
            binding.root,
            (Constants.NO_INTERNET_CONNECTION),
            Snackbar.LENGTH_INDEFINITE
        )
            .setTextColor(Color.BLACK)
            .setBackgroundTint(Color.WHITE)
            .setAction("Retry") {
                if (doubleBackToExitPressedOnce) {
                    splashViewModel.readFireBase()
                    doubleBackToExitPressedOnce = false
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = true
                }, 2000)
            }
            .setActionTextColor(Color.BLACK)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        splashViewModel.cancelViewModelScopeCoroutines()
    }
}