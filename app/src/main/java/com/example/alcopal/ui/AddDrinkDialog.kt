package com.example.alcopal.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.alcopal.R
import com.example.alcopal.data.Drink

class AddDrinkDialog : DialogFragment() {

    private var onDrinkAddedListener: ((Drink) -> Unit)? = null

    // Preset drinks data
    private val presetDrinks = listOf(
        PresetDrink("Beer (355ml)", 5.0, 355.0),
        PresetDrink("Wine Glass (150ml)", 12.0, 150.0),
        PresetDrink("Wine Bottle (750ml)", 12.0, 750.0),
        PresetDrink("Cocktail (60ml)", 40.0, 60.0),
        PresetDrink("Shot (30ml)", 40.0, 30.0),
        PresetDrink("Light Beer (355ml)", 3.5, 355.0),
        PresetDrink("Strong Beer (355ml)", 8.0, 355.0),
        PresetDrink("Champagne Glass (125ml)", 12.0, 125.0),
        PresetDrink("Custom", 0.0, 0.0) // Custom option
    )

    data class PresetDrink(val name: String, val alcoholContent: Double, val volume: Double)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_drink, null)

        val spinnerPresets = view.findViewById<Spinner>(R.id.spinnerPresets)
        val etDrinkName = view.findViewById<EditText>(R.id.etDrinkName)
        val etAlcoholContent = view.findViewById<EditText>(R.id.etAlcoholContent)
        val etVolume = view.findViewById<EditText>(R.id.etVolume)
        val layoutCustom = view.findViewById<LinearLayout>(R.id.layoutCustom)

        // Setup spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            presetDrinks.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPresets.adapter = adapter

        // Handle preset selection
        spinnerPresets.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDrink = presetDrinks[position]

                if (selectedDrink.name == "Custom") {
                    layoutCustom.visibility = View.VISIBLE
                    etDrinkName.setText("")
                    etAlcoholContent.setText("")
                    etVolume.setText("")
                } else {
                    layoutCustom.visibility = View.GONE
                    etDrinkName.setText(selectedDrink.name)
                    etAlcoholContent.setText(selectedDrink.alcoholContent.toString())
                    etVolume.setText(selectedDrink.volume.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Drink")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val name = etDrinkName.text.toString().trim()
                val alcoholContentStr = etAlcoholContent.text.toString().trim()
                val volumeStr = etVolume.text.toString().trim()

                if (validateInput(name, alcoholContentStr, volumeStr)) {
                    val drink = Drink(
                        name = name,
                        alcoholContent = alcoholContentStr.toDouble(),
                        volume = volumeStr.toDouble()
                    )
                    onDrinkAddedListener?.invoke(drink)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun validateInput(name: String, alcoholContent: String, volume: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a drink name", Toast.LENGTH_SHORT).show()
            return false
        }

        val alcoholContentValue = alcoholContent.toDoubleOrNull()
        if (alcoholContentValue == null || alcoholContentValue < 0 || alcoholContentValue > 100) {
            Toast.makeText(requireContext(), "Please enter a valid alcohol content (0-100%)", Toast.LENGTH_SHORT).show()
            return false
        }

        val volumeValue = volume.toDoubleOrNull()
        if (volumeValue == null || volumeValue <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid volume", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun setOnDrinkAddedListener(listener: (Drink) -> Unit) {
        onDrinkAddedListener = listener
    }
}