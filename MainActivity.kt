package com.example.soundmixer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.soundmixer.data_base.AppDatabase
import com.example.soundmixer.databinding.ActivityMainBinding
import com.example.soundmixer.features.merge.view.MergeFragment
import com.example.soundmixer.features.playback.view.PlaybackFragment
import com.example.soundmixer.features.recordings.view.AudioListFragment
import com.example.soundmixer.features.recordings.view.RecordingFragment
import com.example.soundmixer.features.search.view.SearchFragment
import com.example.soundmixer.repository.SoundRepository
import com.example.soundmixer.services.AudioService
import com.example.soundmixer.viewmodels.factory.SoundViewModelFactory
import com.example.soundmixer.viewmodels.SoundViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    // Late init properties for views
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private var backPressedOnce = false // Flag to handle double back press

    // ViewModel for managing recorded files
    private val viewModel: SoundViewModel by viewModels {
        SoundViewModelFactory(
            SoundRepository(AppDatabase.getDatabase(this).recordingDao())
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using view binding to inflate layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize default fragment on app start
        if (savedInstanceState == null) {
            replaceFragment(SearchFragment(), false)
            binding.bottomNavigation.menu.findItem(R.id.navigation_Search).isChecked = true
        }

        bottomNavigation = binding.bottomNavigation

        // Adjust window to resize when keyboard appears and secure the window
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Handle keyboard visibility to hide/show bottom navigation
        handleKeyboardVisibility()

        setupFragmentNavigation() // Set up bottom navigation listener
        createNotificationChannel() // Create notification channel
        requestNotificationPermission() // Request notification permission
    }

    /**
     * Hides the bottom navigation view when the keyboard is visible.
     */
    private fun handleKeyboardVisibility() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) { // Check if keyboard is visible
                hideBottomNavigation()
            } else {
                showBottomNavigation()
            }
        }
    }

    /**
     * Hide the bottom navigation bar.
     */
    fun hideBottomNavigation() {
        bottomNavigation.visibility = View.GONE
    }

    /**
     * Show the bottom navigation bar.
     */
    fun showBottomNavigation() {
        bottomNavigation.visibility = View.VISIBLE
    }

    /**
     * Provides access to the ViewModel for managing recorded files.
     */
    fun obtainViewModel(): SoundViewModel = viewModel

    /**
     * Handles back press behavior. Double press to exit from SearchFragment.
     */
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment is SearchFragment && supportFragmentManager.backStackEntryCount == 0) {
            if (backPressedOnce) {
                super.onBackPressed() // Close the app
                return
            }

            this.backPressedOnce = true
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                backPressedOnce = false
            }, 2000) // Reset flag after 2 seconds
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Sets up the bottom navigation listener for fragment transactions.
     */
    private fun setupFragmentNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment = when (menuItem.itemId) {
                R.id.navigation_Search -> SearchFragment()
                R.id.navigation_Record -> RecordingFragment()
                R.id.playback_screen -> PlaybackFragment()
                R.id.navigation_Files -> AudioListFragment()
                R.id.navigation_merge -> MergeFragment()
                else -> return@setOnNavigationItemSelectedListener false
            }

            replaceFragment(fragment, menuItem.itemId != R.id.navigation_Search)
            true
        }

        handleBackStack()
    }

    /**
     * Updates the bottom navigation based on the visible fragment.
     */
    private fun updateBottomNavigation(fragment: Fragment) {
        when (fragment) {
            is SearchFragment -> binding.bottomNavigation.menu.findItem(R.id.navigation_Search).isChecked = true
            is RecordingFragment -> binding.bottomNavigation.menu.findItem(R.id.navigation_Record).isChecked = true
            is PlaybackFragment -> binding.bottomNavigation.menu.findItem(R.id.playback_screen).isChecked = true
            is AudioListFragment -> binding.bottomNavigation.menu.findItem(R.id.navigation_Files).isChecked = true
            is MergeFragment -> binding.bottomNavigation.menu.findItem(R.id.navigation_merge).isChecked = true
        }
    }

    /**
     * Handles fragment back stack and keeps bottom navigation in sync.
     */
    private fun handleBackStack() {
        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            fragment?.let { updateBottomNavigation(it) }
        }
    }

    /**
     * Replaces the current fragment and optionally adds it to the back stack.
     */
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment is SearchFragment) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            replace(R.id.fragment_container, fragment)
            if (addToBackStack) addToBackStack(fragment::class.java.simpleName)
            commit()
        }
    }

    /**
     * Creates a notification channel for Android O and above.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Audio Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    /**
     * Requests notification permission for Android 13 and above.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * Handles the result of the notification permission request.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            val message = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                "Notification permission granted"
            } else {
                "Notification permission denied"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CHANNEL_ID = "audio_service_channel"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(this, AudioService::class.java).also {
            it.action = AudioService.ACTION_STOP_PLAYBACK
            this.stopService(it)
        }
    }
}




