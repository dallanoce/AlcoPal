package com.example.alcopal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.layout.layout
import androidx.recyclerview.widget.RecyclerView
import com.example.alcopal.data.Drink
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private var drinks: List<Drink>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Assuming item_drink_history.xml has a TextView with this ID
        val drinkInfoTextView: TextView = view.findViewById(R.id.drinkInfoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drink_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drink = drinks[position]
        val formattedTimestamp =
            SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(drink.timestamp))
        holder.drinkInfoTextView.text =
            "${drink.name} (${drink.volume}ml, ${drink.alcoholContent}%) - ${formattedTimestamp}"
    }

    override fun getItemCount() = drinks.size

    fun updateDrinks(newDrinks: List<Drink>) {
        drinks = newDrinks
        notifyDataSetChanged() // For simplicity, consider DiffUtil for better performance later
    }
}