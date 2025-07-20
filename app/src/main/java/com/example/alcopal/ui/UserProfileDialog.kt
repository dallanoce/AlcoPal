package com.example.alcopal.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.alcopal.R
import com.example.alcopal.data.Gender
import com.example.alcopal.data.UserProfile

class UserProfileDialog : DialogFragment() {

    private var onProfileSetListener: ((UserProfile) -> Unit)? = null
    private var currentProfile: UserProfile? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user_profile, null)

        val etWeight = view.findViewById<EditText>(R.id.etWeight)
        val radioGroupGender = view.findViewById<RadioGroup>(R.id.radioGroupGender)
        val radioMale = view.findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = view.findViewById<RadioButton>(R.id.radioFemale)
        val etAge = view.findViewById<EditText>(R.id.etAge)

        // Pre-fill with current profile if available
        currentProfile?.let { profile ->
            etWeight.setText(profile.weight.toString())
            etAge.setText(profile.age.toString())
            when (profile.gender) {
                Gender.MALE -> radioMale.isChecked = true
                Gender.FEMALE -> radioFemale.isChecked = true
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Set Your Profile")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val weightStr = etWeight.text.toString().trim()
                val ageStr = etAge.text.toString().trim()
                val selectedGenderId = radioGroupGender.checkedRadioButtonId

                if (validateInput(weightStr, ageStr, selectedGenderId)) {
                    val gender = if (selectedGenderId == R.id.radioMale) Gender.MALE else Gender.FEMALE

                    val profile = UserProfile(
                        weight = weightStr.toDouble(),
                        gender = gender,
                        age = ageStr.toInt()
                    )
                    onProfileSetListener?.invoke(profile)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun validateInput(weight: String, age: String, genderId: Int): Boolean {
        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue < 30 || weightValue > 300) {
            Toast.makeText(requireContext(), "Please enter a valid weight (30-300 kg)", Toast.LENGTH_SHORT).show()
            return false
        }

        val ageValue = age.toIntOrNull()
        if (ageValue == null || ageValue < 18 || ageValue > 100) {
            Toast.makeText(requireContext(), "Please enter a valid age (18-100 years)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (genderId == -1) {
            Toast.makeText(requireContext(), "Please select your gender", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun setOnProfileSetListener(listener: (UserProfile) -> Unit) {
        onProfileSetListener = listener
    }

    fun setCurrentProfile(profile: UserProfile?) {
        currentProfile = profile
    }
}