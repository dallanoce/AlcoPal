package com.example.alcopal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alcopal.ui.BACViewModel
import com.example.alcopal.ui.AddDrinkDialog
import com.example.alcopal.ui.UserProfileDialog
import com.example.alcopal.data.UserProfile
import com.example.alcopal.data.Gender
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: BACViewModel // Made public for HomeFragment access

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[BACViewModel::class.java]

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        bottomNavView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.navigation_profile -> {
                    selectedFragment = ProfileFragment()
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        // Only set default profile if none exists
        if (viewModel.getUserProfile() == null) {
            setupDefaultProfile()
        }
    }

    fun showAddDrinkDialog() { // Made public for HomeFragment access
        val dialog = AddDrinkDialog()
        dialog.setOnDrinkAddedListener { drink ->
            viewModel.addDrink(drink)
        }
        dialog.show(supportFragmentManager, "add_drink")
    }

    fun showUserProfileDialog() { // Made public for HomeFragment access
        val dialog = UserProfileDialog()
        dialog.setCurrentProfile(viewModel.getUserProfile())
        dialog.setOnProfileSetListener { profile ->
            viewModel.setUserProfile(profile)
        }
        dialog.show(supportFragmentManager, "user_profile")
    }

    private fun setupDefaultProfile() {
        val defaultProfile = UserProfile(
            weight = 70.0, // 70kg
            gender = Gender.MALE,
            age = 25
        )
        viewModel.setUserProfile(defaultProfile)
    }

    // formatTime is not directly used by MainActivity anymore, can be moved or kept if other parts of activity need it.
    // For now, it will be moved to HomeFragment as it was used for UI there.
}