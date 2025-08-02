package com.example.alcopal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alcopal.ui.BACViewModel
import java.text.DecimalFormat

class HomeFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // ViewModel is now accessed from the Activity
        viewModel = (activity as MainActivity).viewModel

        initViews(view)
        setupClickListeners()
        observeViewModel()

        return view
    }

    private fun initViews(view: View) {
        tvBacValue = view.findViewById(R.id.tvBacValue)
        tvBacStatus = view.findViewById(R.id.tvBacStatus)
        tvDrinkCount = view.findViewById(R.id.tvDrinkCount)
        tvTimeToSober = view.findViewById(R.id.tvTimeToSober)
        tvTimeToLegal = view.findViewById(R.id.tvTimeToLegal)
        tvSetupPrompt = view.findViewById(R.id.tvSetupPrompt)
        btnAddDrink = view.findViewById(R.id.btnAddDrink)
        btnClearDrinks = view.findViewById(R.id.btnClearDrinks)
        btnSetProfile = view.findViewById(R.id.btnSetProfile)
    }

    private fun setupClickListeners() {
        btnAddDrink.setOnClickListener {
            (activity as MainActivity).showAddDrinkDialog()
        }

        btnClearDrinks.setOnClickListener {
            viewModel.clearDrinks()
        }

        btnSetProfile.setOnClickListener {
            (activity as MainActivity).showUserProfileDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.currentBAC.observe(viewLifecycleOwner) { bacReading ->
            val decimalFormat = DecimalFormat("0.000")
            tvBacValue.text = decimalFormat.format(bacReading.bac)
            tvBacStatus.text = bacReading.status.description
            // tvBacStatus.setTextColor(bacReading.status.color) // TODO: Ensure color resource is handled if needed
        }

        viewModel.drinkCount.observe(viewLifecycleOwner) { count ->
            tvDrinkCount.text = "Drinks: $count"
        }

        viewModel.timeToSober.observe(viewLifecycleOwner) { timeMs ->
            tvTimeToSober.text = if (timeMs > 0) {
                "Time to sober: ${formatTime(timeMs)}"
            } else {
                "You\'re sober!"
            }
        }

        viewModel.timeToLegal.observe(viewLifecycleOwner) { timeMs ->
            tvTimeToLegal.text = if (timeMs > 0) {
                "Time to legal limit: ${formatTime(timeMs)}"
            } else {
                "Below legal limit"
            }
        }

        viewModel.hasUserProfile.observe(viewLifecycleOwner) { hasProfile ->
            val visibility = if (hasProfile) View.VISIBLE else View.GONE
            tvDrinkCount.visibility = visibility
            tvTimeToSober.visibility = visibility
            tvTimeToLegal.visibility = visibility
            btnAddDrink.visibility = visibility
            btnClearDrinks.visibility = visibility
            tvSetupPrompt.visibility = if (hasProfile) View.GONE else View.VISIBLE
        }
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