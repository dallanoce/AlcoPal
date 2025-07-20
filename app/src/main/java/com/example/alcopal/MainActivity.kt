package com.example.alcopal

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.alcopal.ui.BACViewModel
import com.example.alcopal.ui.AddDrinkDialog
import com.example.alcopal.ui.UserProfileDialog
import com.example.alcopal.data.UserProfile
import com.example.alcopal.data.Gender
import com.example.alcopal.data.Drink
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BACViewModel

    // Views
    private lateinit var tvBacValue: TextView
    private lateinit var tvBacStatus: TextView
    private lateinit var tvDrinkCount: TextView
    private lateinit var tvTimeToSober: TextView
    private lateinit var tvTimeToLegal: TextView
    private lateinit var tvSetupPrompt: TextView
    private lateinit var btnAddDrink: Button
    private lateinit var btnClearDrinks: Button
    private lateinit var btnSetProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[BACViewModel::class.java]

        initViews()
        setupClickListeners()
        observeViewModel()

        // Only set default profile if none exists
        if (viewModel.getUserProfile() == null) {
            setupDefaultProfile()
        }
    }

    private fun initViews() {
        tvBacValue = findViewById(R.id.tvBacValue)
        tvBacStatus = findViewById(R.id.tvBacStatus)
        tvDrinkCount = findViewById(R.id.tvDrinkCount)
        tvTimeToSober = findViewById(R.id.tvTimeToSober)
        tvTimeToLegal = findViewById(R.id.tvTimeToLegal)
        tvSetupPrompt = findViewById(R.id.tvSetupPrompt)
        btnAddDrink = findViewById(R.id.btnAddDrink)
        btnClearDrinks = findViewById(R.id.btnClearDrinks)
        btnSetProfile = findViewById(R.id.btnSetProfile)
    }

    private fun setupClickListeners() {
        btnAddDrink.setOnClickListener {
            showAddDrinkDialog()
        }

        btnClearDrinks.setOnClickListener {
            viewModel.clearDrinks()
        }

        btnSetProfile.setOnClickListener {
            showUserProfileDialog()
        }
    }

    private fun showAddDrinkDialog() {
        val dialog = AddDrinkDialog()
        dialog.setOnDrinkAddedListener { drink ->
            viewModel.addDrink(drink)
        }
        dialog.show(supportFragmentManager, "add_drink")
    }

    private fun showUserProfileDialog() {
        val dialog = UserProfileDialog()
        dialog.setCurrentProfile(viewModel.getUserProfile())
        dialog.setOnProfileSetListener { profile ->
            viewModel.setUserProfile(profile)
        }
        dialog.show(supportFragmentManager, "user_profile")
    }

    private fun observeViewModel() {
        viewModel.currentBAC.observe(this) { bacReading ->
            val decimalFormat = DecimalFormat("0.000")
            tvBacValue.text = decimalFormat.format(bacReading.bac)
            tvBacStatus.text = bacReading.status.description

            // You can set colors here if needed
            // tvBacStatus.setTextColor(bacReading.status.color)
        }

        viewModel.drinkCount.observe(this) { count ->
            tvDrinkCount.text = "Drinks: $count"
        }

        viewModel.timeToSober.observe(this) { timeMs ->
            tvTimeToSober.text = if (timeMs > 0) {
                "Time to sober: ${formatTime(timeMs)}"
            } else {
                "You're sober!"
            }
        }

        viewModel.timeToLegal.observe(this) { timeMs ->
            tvTimeToLegal.text = if (timeMs > 0) {
                "Time to legal limit: ${formatTime(timeMs)}"
            } else {
                "Below legal limit"
            }
        }

        viewModel.hasUserProfile.observe(this) { hasProfile ->
            // Show/hide UI elements based on whether user has set up profile
            val visibility = if (hasProfile) View.VISIBLE else View.GONE
            tvDrinkCount.visibility = visibility
            tvTimeToSober.visibility = visibility
            tvTimeToLegal.visibility = visibility
            btnAddDrink.visibility = visibility
            btnClearDrinks.visibility = visibility

            tvSetupPrompt.visibility = if (hasProfile) View.GONE else View.VISIBLE
        }
    }

    private fun setupDefaultProfile() {
        // Set a default profile so the app works immediately
        val defaultProfile = UserProfile(
            weight = 70.0, // 70kg
            gender = Gender.MALE,
            age = 25
        )
        viewModel.setUserProfile(defaultProfile)
    }

    private fun formatTime(timeMs: Long): String {
        val hours = timeMs / (1000 * 60 * 60)
        val minutes = (timeMs % (1000 * 60 * 60)) / (1000 * 60)

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}