package com.example.alcopal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alcopal.ui.BACViewModel // Assuming this is your ViewModel path
// import com.example.alcopal.model.UserProfile // Assuming UserProfile model path

class ProfileFragment : Fragment() {

    private lateinit var viewModel: BACViewModel
    private lateinit var tvProfileWeight: TextView
    private lateinit var tvProfileGender: TextView
    private lateinit var tvProfileAge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        viewModel = (activity as MainActivity).viewModel

        tvProfileWeight = view.findViewById(R.id.tvProfileWeight)
        tvProfileGender = view.findViewById(R.id.tvProfileGender)
        tvProfileAge = view.findViewById(R.id.tvProfileAge)

        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                tvProfileWeight.text = "Weight: ${userProfile.weight} kg"
                tvProfileGender.text = "Gender: ${userProfile.gender}" // Adjust if gender is an enum
                tvProfileAge.text = "Age: ${userProfile.age}"
            } else {
                tvProfileWeight.text = "Weight: Not set"
                tvProfileGender.text = "Gender: Not set"
                tvProfileAge.text = "Age: Not set"
            }
        }
        // Removed observation of hasUserProfile
    }
}
